package com.school.api.activite.service.impl;

import com.school.api.activite.dto.*;
import com.school.api.activite.entity.*;
import com.school.api.activite.repository.*;
import com.school.api.activite.service.ActiviteService;
import com.school.api.banner.entity.MediaType;
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

  /* ======================= CREATE ======================= */

  @Override
  public ActiviteResponse create(
    ActiviteRequest request,
    MultipartFile[] photos,
    MultipartFile video
  ) {

    if (photos == null || photos.length == 0) {
      throw new IllegalArgumentException("Au moins une photo est obligatoire");
    }

    Activite activite = new Activite();
    activite.setTitre(request.getTitre());
    activite.setContenu(request.getContenu());
    activiteRepository.save(activite);

    /* ===================== PHOTOS ===================== */
    for (MultipartFile photo : photos) {

      String imageUrl = fileStorageService.storeActualiteGalleryImage(photo);

      ActiviteImage image = new ActiviteImage();
      image.setImageUrl(imageUrl);
      image.setType(ActiviteMediaType.IMAGE);
      image.setActivite(activite);

      activiteImageRepository.save(image);
      activite.getImages().add(image);
    }

    /* ===================== VIDEO ====================== */
    if (video != null && !video.isEmpty()) {

      String videoUrl =
        fileStorageService.storeBannerMedia(video, MediaType.VIDEO);

      ActiviteImage videoEntity = new ActiviteImage();
      videoEntity.setImageUrl(videoUrl);
      videoEntity.setType(ActiviteMediaType.VIDEO);
      videoEntity.setActivite(activite);

      activiteImageRepository.save(videoEntity);
      activite.getImages().add(videoEntity);
    }

    return mapToResponse(activite);
  }

  /* ======================== READ ======================= */

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

  /* ======================= DELETE ====================== */

  @Override
  public void delete(Long id) {

    Activite activite = activiteRepository.findById(id)
      .orElseThrow(() ->
        new ResourceNotFoundException("Activité introuvable")
      );

    activite.getImages().forEach(
      img -> fileStorageService.deleteQuietly(img.getImageUrl())
    );

    activiteRepository.delete(activite);
  }

  @Override
  public void deleteMedia(Long mediaId) {

    ActiviteImage image = activiteImageRepository.findById(mediaId)
      .orElseThrow(() ->
        new ResourceNotFoundException("Média introuvable")
      );

    fileStorageService.deleteQuietly(image.getImageUrl());
    activiteImageRepository.delete(image);
  }

  /* ====================== MAPPING ====================== */

  private ActiviteResponse mapToResponse(Activite activite) {

    ActiviteResponse response = new ActiviteResponse();
    response.setId(activite.getId());
    response.setTitre(activite.getTitre());
    response.setContenu(activite.getContenu());

    response.setMedias(
      activite.getImages().stream()
        .map(img -> {
          ActiviteMediaResponse media = new ActiviteMediaResponse();
          media.setId(img.getId());
          media.setUrl(img.getImageUrl());
          media.setType(img.getType());
          return media;
        })
        .toList()
    );

    return response;
  }
}
