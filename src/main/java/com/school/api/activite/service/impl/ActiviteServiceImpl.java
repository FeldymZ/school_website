package com.school.api.activite.service.impl;

import com.school.api.activite.dto.ActiviteImageResponse;
import com.school.api.activite.dto.ActiviteRequest;
import com.school.api.activite.dto.ActiviteResponse;
import com.school.api.activite.entity.Activite;
import com.school.api.activite.entity.ActiviteImage;
import com.school.api.activite.repository.ActiviteImageRepository;
import com.school.api.activite.repository.ActiviteRepository;
import com.school.api.activite.service.ActiviteService;
import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.common.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ActiviteServiceImpl implements ActiviteService {

  private final ActiviteRepository activiteRepository;
  private final ActiviteImageRepository activiteImageRepository;
  private final FileStorageService fileStorageService;

  /* ========================= CREATE ========================= */

  @Override
  public ActiviteResponse create(ActiviteRequest request, MultipartFile photo) {

    if (photo == null || photo.isEmpty()) {
      throw new IllegalArgumentException("La photo est obligatoire");
    }

    Activite activite = Activite.builder()
      .titre(request.getTitre())
      .contenu(request.getContenu())
      .build();

    activiteRepository.save(activite);

    String imageUrl = fileStorageService.storeActualiteGalleryImage(photo);

    ActiviteImage image = ActiviteImage.builder()
      .fileName(imageUrl) // URL publique /files/...
      .activite(activite)
      .build();

    activiteImageRepository.save(image);
    activite.getImages().add(image);

    return mapToResponse(activite);
  }

  /* ========================= UPDATE ========================= */

  @Override
  public ActiviteResponse update(Long id, ActiviteRequest request, MultipartFile photo) {

    Activite activite = activiteRepository.findById(id)
      .orElseThrow(() ->
        new ResourceNotFoundException("Activité introuvable")
      );

    activite.setTitre(request.getTitre());
    activite.setContenu(request.getContenu());

    if (photo != null && !photo.isEmpty()) {

      String imageUrl = fileStorageService.storeActualiteGalleryImage(photo);

      ActiviteImage image = ActiviteImage.builder()
        .fileName(imageUrl)
        .activite(activite)
        .build();

      activiteImageRepository.save(image);
      activite.getImages().add(image);
    }

    return mapToResponse(activite);
  }

  /* ========================= DELETE ========================= */

  @Override
  public void delete(Long id) {

    Activite activite = activiteRepository.findById(id)
      .orElseThrow(() ->
        new ResourceNotFoundException("Activité introuvable")
      );

    activite.getImages().forEach(
      img -> fileStorageService.deleteQuietly(img.getFileName())
    );

    activiteRepository.delete(activite);
  }

  /* ====================== DELETE IMAGE ====================== */

  @Override
  public void deleteImage(Long imageId) {

    ActiviteImage image = activiteImageRepository.findById(imageId)
      .orElseThrow(() ->
        new ResourceNotFoundException("Image introuvable")
      );

    fileStorageService.deleteQuietly(image.getFileName());
    activiteImageRepository.delete(image);
  }

  /* ========================= READ ========================= */

  @Override
  public List<ActiviteResponse> getAll() {
    return activiteRepository.findAll()
      .stream()
      .map(this::mapToResponse)
      .toList();
  }

  @Override
  public ActiviteResponse getById(Long id) {

    Activite activite = activiteRepository.findById(id)
      .orElseThrow(() ->
        new ResourceNotFoundException("Activité introuvable")
      );

    return mapToResponse(activite);
  }

  /* ======================== MAPPER ========================= */

  private ActiviteResponse mapToResponse(Activite activite) {

    return ActiviteResponse.builder()
      .id(activite.getId())
      .titre(activite.getTitre())
      .contenu(activite.getContenu())
      .images(
        activite.getImages().stream()
          .map(img -> ActiviteImageResponse.builder()
            .id(img.getId())
            .url(img.getFileName())
            .build()
          )
          .toList()
      )
      .build();
  }
}
