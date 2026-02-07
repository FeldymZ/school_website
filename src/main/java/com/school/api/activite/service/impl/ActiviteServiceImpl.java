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

import java.text.Normalizer;
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

  /* ================= SLUG ================= */
  private String generateSlug(String input) {
    return Normalizer.normalize(input, Normalizer.Form.NFD)
      .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
      .toLowerCase()
      .replaceAll("[^a-z0-9]+", "-")
      .replaceAll("(^-|-$)", "");
  }

  /* ================= CREATE ================= */
  @Override
  public ActiviteResponse create(
    ActiviteRequest request,
    MultipartFile[] photos,
    MultipartFile video
  ) {
    Activite activite = new Activite();
    activite.setTitre(request.getTitre());
    activite.setContenu(request.getContenu());

    String baseSlug = generateSlug(request.getTitre());
    String slug = baseSlug;
    int i = 1;

    while (activiteRepository.existsBySlug(slug)) {
      slug = baseSlug + "-" + i++;
    }

    activite.setSlug(slug);
    activiteRepository.save(activite);

    addMediasInternal(activite, photos, video);

    return mapToAdminResponse(activite);
  }

  /* ================= ADD MEDIAS ================= */
  @Override
  public ActiviteResponse addMedias(
    Long activiteId,
    MultipartFile[] photos,
    MultipartFile video
  ) {
    Activite activite = activiteRepository.findById(activiteId)
      .orElseThrow(() ->
        new ResourceNotFoundException("Activité introuvable")
      );

    addMediasInternal(activite, photos, video);

    return mapToAdminResponse(activite);
  }

  /* ================= INTERNAL MEDIA HANDLER ================= */
  private void addMediasInternal(
    Activite activite,
    MultipartFile[] photos,
    MultipartFile video
  ) {
    /* ===== IMAGES ===== */
    if (photos != null) {
      for (MultipartFile photo : photos) {
        if (photo == null || photo.isEmpty()) continue;

        String url =
          fileStorageService.storeActualiteGalleryImage(photo);

        ActiviteImage img = new ActiviteImage();
        img.setActivite(activite);
        img.setImageUrl(url);
        img.setType(ActiviteMediaType.IMAGE);

        activiteImageRepository.save(img);
        activite.getImages().add(img);
      }
    }

    /* ===== VIDEO (UNIQUE, REMPLACEMENT) ===== */
    if (video != null && !video.isEmpty()) {

      // suppression ancienne vidéo
      activite.getImages().stream()
        .filter(m -> m.getType() == ActiviteMediaType.VIDEO)
        .forEach(m -> {
          fileStorageService.deleteQuietly(m.getImageUrl());
          activiteImageRepository.delete(m);
        });

      String videoUrl =
        fileStorageService.storeBannerMedia(video, MediaType.VIDEO);

      ActiviteImage vid = new ActiviteImage();
      vid.setActivite(activite);
      vid.setImageUrl(videoUrl);
      vid.setType(ActiviteMediaType.VIDEO);

      activiteImageRepository.save(vid);
      activite.getImages().add(vid);
    }
  }

  /* ================= READ ================= */
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
      .orElseThrow(() ->
        new ResourceNotFoundException("Activité introuvable")
      );
    return mapToAdminResponse(activite);
  }

  /* ================= DELETE ================= */
  @Override
  public void delete(Long id) {
    Activite activite = activiteRepository.findById(id)
      .orElseThrow(() ->
        new ResourceNotFoundException("Activité introuvable")
      );

    activite.getImages()
      .forEach(img ->
        fileStorageService.deleteQuietly(img.getImageUrl())
      );

    activiteRepository.delete(activite);
  }

  @Override
  public void deleteMedia(Long mediaId) {
    ActiviteImage img = activiteImageRepository.findById(mediaId)
      .orElseThrow(() ->
        new ResourceNotFoundException("Média introuvable")
      );

    fileStorageService.deleteQuietly(img.getImageUrl());
    activiteImageRepository.delete(img);
  }

  /* ================= PUBLIC ================= */
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
      .orElseThrow(() ->
        new ResourceNotFoundException("Activité introuvable")
      );
    return mapToPublicResponse(activite);
  }

  /* ================= MAPPERS ================= */
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
        ActiviteMediaPublicResponse m =
          new ActiviteMediaPublicResponse();
        m.setUrl(img.getImageUrl());
        m.setType(img.getType());
        return m;
      }).toList()
    );

    return res;
  }
}
