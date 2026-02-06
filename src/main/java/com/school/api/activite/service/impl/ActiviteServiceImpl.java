package com.school.api.activite.service.impl;

import com.school.api.activite.dto.*;
import com.school.api.activite.entity.*;
import com.school.api.activite.repository.*;
import com.school.api.activite.service.ActiviteService;
import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.common.storage.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class ActiviteServiceImpl implements ActiviteService {

  private final ActiviteRepository activiteRepository;
  private final ActiviteImageRepository activiteImageRepository;
  private final FileStorageService fileStorageService;

  public ActiviteServiceImpl(
    ActiviteRepository activiteRepository,
    ActiviteImageRepository activiteImageRepository,
    FileStorageService fileStorageService
  ) {
    this.activiteRepository = activiteRepository;
    this.activiteImageRepository = activiteImageRepository;
    this.fileStorageService = fileStorageService;
  }

  @Override
  public ActiviteResponse create(ActiviteRequest request, MultipartFile photo) {

    if (photo == null || photo.isEmpty()) {
      throw new IllegalArgumentException("La photo est obligatoire");
    }

    Activite activite = new Activite();
    activite.setTitre(request.getTitre());
    activite.setContenu(request.getContenu());

    activiteRepository.save(activite);

    String imageUrl = fileStorageService.storeActualiteGalleryImage(photo);

    ActiviteImage image = new ActiviteImage();
    image.setFileName(imageUrl);
    image.setActivite(activite);

    activiteImageRepository.save(image);
    activite.getImages().add(image);

    return mapToResponse(activite);
  }

  @Override
  public ActiviteResponse update(Long id, ActiviteRequest request, MultipartFile photo) {

    Activite activite = activiteRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Activité introuvable"));

    activite.setTitre(request.getTitre());
    activite.setContenu(request.getContenu());

    if (photo != null && !photo.isEmpty()) {
      String imageUrl = fileStorageService.storeActualiteGalleryImage(photo);

      ActiviteImage image = new ActiviteImage();
      image.setFileName(imageUrl);
      image.setActivite(activite);

      activiteImageRepository.save(image);
      activite.getImages().add(image);
    }

    return mapToResponse(activite);
  }

  @Override
  public void delete(Long id) {

    Activite activite = activiteRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Activité introuvable"));

    activite.getImages()
      .forEach(img -> fileStorageService.deleteQuietly(img.getFileName()));

    activiteRepository.delete(activite);
  }

  @Override
  public void deleteImage(Long imageId) {

    ActiviteImage image = activiteImageRepository.findById(imageId)
      .orElseThrow(() -> new ResourceNotFoundException("Image introuvable"));

    fileStorageService.deleteQuietly(image.getFileName());
    activiteImageRepository.delete(image);
  }

  @Override
  public List<ActiviteResponse> getAll() {
    return activiteRepository.findAll().stream()
      .map(this::mapToResponse)
      .toList();
  }

  @Override
  public ActiviteResponse getById(Long id) {

    Activite activite = activiteRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Activité introuvable"));

    return mapToResponse(activite);
  }

  private ActiviteResponse mapToResponse(Activite activite) {

    ActiviteResponse response = new ActiviteResponse();
    response.setId(activite.getId());
    response.setTitre(activite.getTitre());
    response.setContenu(activite.getContenu());

    response.setImages(
      activite.getImages().stream()
        .map(img -> {
          ActiviteImageResponse r = new ActiviteImageResponse();
          r.setId(img.getId());
          r.setUrl(img.getFileName());
          return r;
        })
        .toList()
    );

    return response;
  }
}
