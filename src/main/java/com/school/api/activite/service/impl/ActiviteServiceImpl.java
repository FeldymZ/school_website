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

  /**
   * Génère un slug à partir d'une chaîne en minuscule,
   * remplaçant les caractères non alphanumériques par des tirets,
   * sans tirets en début ou fin.
   */
  private String generateSlug(String input) {
    return input
      .toLowerCase()
      .replaceAll("[^a-z0-9]+", "-")
      .replaceAll("(^-|-$)", "");
  }

  @Override
  public ActiviteResponse create(
    ActiviteRequest request,
    MultipartFile[] photos,
    MultipartFile video
  ) {

    Activite activite = new Activite();
    activite.setTitre(request.getTitre());
    activite.setContenu(request.getContenu());

    // Génération du slug unique
    String baseSlug = generateSlug(request.getTitre());
    String slug = baseSlug;
    int i = 1;
    while (activiteRepository.existsBySlug(slug)) {
      slug = baseSlug + "-" + i++;
    }
    activite.setSlug(slug);

    activiteRepository.save(activite);

    // Sauvegarde des photos associées
    for (MultipartFile photo : photos) {
      String url = fileStorageService.storeActualiteGalleryImage(photo);

      ActiviteImage img = new ActiviteImage();
      img.setImageUrl(url);
      img.setType(ActiviteMediaType.IMAGE);
      img.setActivite(activite);

      activiteImageRepository.save(img);
      activite.getImages().add(img);
    }

    // Sauvegarde de la vidéo associée si présente
    if (video != null && !video.isEmpty()) {
      String videoUrl = fileStorageService.storeBannerMedia(video, MediaType.VIDEO);

      ActiviteImage vid = new ActiviteImage();
      vid.setImageUrl(videoUrl);
      vid.setType(ActiviteMediaType.VIDEO);
      vid.setActivite(activite);

      activiteImageRepository.save(vid);
      activite.getImages().add(vid);
    }

    return mapToAdminResponse(activite);
  }

  @Override
  public List<ActiviteResponse> getAll() {
    return activiteRepository.findAll()
      .stream()
      .map(this::mapToAdminResponse)
      .toList();
  }

  @Override
  public ActiviteResponse getById(Long id) {
    Activite activite = activiteRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Activité introuvable"));
    return mapToAdminResponse(activite);
  }

  @Override
  public void delete(Long id) {
    Activite activite = activiteRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Activité introuvable"));

    // Suppression des fichiers médias liés
    activite.getImages().forEach(
      img -> fileStorageService.deleteQuietly(img.getImageUrl())
    );

    activiteRepository.delete(activite);
  }

  @Override
  public void deleteMedia(Long mediaId) {
    ActiviteImage img = activiteImageRepository.findById(mediaId)
      .orElseThrow(() -> new ResourceNotFoundException("Média introuvable"));

    fileStorageService.deleteQuietly(img.getImageUrl());
    activiteImageRepository.delete(img);
  }

  @Override
  public List<ActivitePublicResponse> getAllPublic() {
    return activiteRepository.findAll()
      .stream()
      .map(this::mapToPublicResponse)
      .toList();
  }

  @Override
  public ActivitePublicResponse getPublicBySlug(String slug) {
    Activite activite = activiteRepository.findBySlug(slug)
      .orElseThrow(() -> new ResourceNotFoundException("Activité introuvable"));
    return mapToPublicResponse(activite);
  }

  private ActiviteResponse mapToAdminResponse(Activite activite) {
    ActiviteResponse res = new ActiviteResponse();
    res.setId(activite.getId());
    res.setTitre(activite.getTitre());
    res.setContenu(activite.getContenu());
    res.setSlug(activite.getSlug());

    res.setMedias(
      activite.getImages().stream().map(img -> {
        ActiviteMediaResponse m = new ActiviteMediaResponse();
        m.setId(img.getId());
        m.setUrl(img.getImageUrl());
        m.setType(img.getType());
        return m;
      }).toList()
    );

    return res;
  }

  private ActivitePublicResponse mapToPublicResponse(Activite activite) {
    ActivitePublicResponse res = new ActivitePublicResponse();
    res.setId(activite.getId());
    res.setTitre(activite.getTitre());
    res.setContenu(activite.getContenu());
    res.setSlug(activite.getSlug());

    res.setMedias(
      activite.getImages().stream().map(img -> {
        ActiviteMediaPublicResponse m = new ActiviteMediaPublicResponse();
        m.setUrl(img.getImageUrl());
        m.setType(img.getType());
        return m;
      }).toList()
    );

    return res;
  }
}
