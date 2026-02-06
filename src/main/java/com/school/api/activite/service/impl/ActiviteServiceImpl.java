package com.school.api.activite.service.impl;

import com.school.api.activite.dto.*;
import com.school.api.activite.entity.*;
import com.school.api.activite.repository.*;
import com.school.api.activite.service.ActiviteService;
import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.common.storage.FileStorageService;
import com.school.api.banner.entity.MediaType;
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

    // PHOTOS
    for (MultipartFile photo : photos) {
      String url = fileStorageService.storeActualiteGalleryImage(photo);
      ActiviteImage img = new ActiviteImage();
      img.setFileUrl(url);
      img.setType(ActiviteMediaType.IMAGE);
      img.setActivite(activite);
      activiteImageRepository.save(img);
      activite.getImages().add(img);
    }

    // VIDEO (optionnelle)
    if (video != null && !video.isEmpty()) {
      String url = fileStorageService.storeBannerMedia(video, MediaType.VIDEO);
      ActiviteImage vid = new ActiviteImage();
      vid.setFileUrl(url);
      vid.setType(ActiviteMediaType.VIDEO);
      vid.setActivite(activite);
      activiteImageRepository.save(vid);
      activite.getImages().add(vid);
    }

    return map(activite);
  }

  @Override
  public List<ActiviteResponse> getAll() {
    return activiteRepository.findAll().stream().map(this::map).toList();
  }

  @Override
  public ActiviteResponse getById(Long id) {
    return map(
      activiteRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Activité introuvable"))
    );
  }

  @Override
  public void delete(Long id) {
    Activite activite = activiteRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Activité introuvable"));

    activite.getImages().forEach(
      img -> fileStorageService.deleteQuietly(img.getFileUrl())
    );

    activiteRepository.delete(activite);
  }

  @Override
  public void deleteMedia(Long mediaId) {
    ActiviteImage img = activiteImageRepository.findById(mediaId)
      .orElseThrow(() -> new ResourceNotFoundException("Média introuvable"));

    fileStorageService.deleteQuietly(img.getFileUrl());
    activiteImageRepository.delete(img);
  }

  private ActiviteResponse map(Activite activite) {

    ActiviteResponse r = new ActiviteResponse();
    r.setId(activite.getId());
    r.setTitre(activite.getTitre());
    r.setContenu(activite.getContenu());

    r.setMedias(
      activite.getImages().stream().map(img -> {
        ActiviteMediaResponse m = new ActiviteMediaResponse();
        m.setId(img.getId());
        m.setUrl(img.getFileUrl());
        m.setType(img.getType());
        return m;
      }).toList()
    );

    return r;
  }
}
