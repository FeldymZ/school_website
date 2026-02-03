package com.school.api.actualite.service;

import com.school.api.actualite.dto.*;
import com.school.api.actualite.entity.*;
import com.school.api.actualite.repository.*;
import com.school.api.common.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActualiteService {

  private final ActualiteRepository repository;
  private final ActualiteImageRepository imageRepository;
  private final ActualitePublicationHistoryRepository historyRepository;
  private final FileStorageService fileStorageService;

  /* ============================
     🌍 PUBLIC
     ============================ */

  public List<ActualiteResponse> getPublic() {
    return repository.findPublicVisible()
      .stream()
      .map(this::toListDto)
      .toList();
  }

  public ActualiteDetailsResponse getDetailsBySlug(String slug) {

    Actualite actualite = repository.findBySlug(slug)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Actualité introuvable"
      ));

    if (!Boolean.TRUE.equals(actualite.getEnabled())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return getDetails(actualite.getId());
  }

  public String getSlugById(Long id) {
    return repository.findById(id)
      .map(Actualite::getSlug)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Actualité introuvable"
      ));
  }

  /* ============================
     🔐 ADMIN / PUBLIC
     ============================ */

  public ActualiteDetailsResponse getDetails(Long id) {

    Actualite actualite = get(id);

    List<ActualiteImage> images =
      imageRepository.findByActualiteIdOrderByDisplayOrderAsc(id);

    List<String> publicImages = images.stream()
      .map(ActualiteImage::getImageUrl)
      .toList();

    List<ActualiteGalleryImageResponse> adminImages = images.stream()
      .map(img -> ActualiteGalleryImageResponse.builder()
        .id(img.getId())
        .url(img.getImageUrl())
        .displayOrder(img.getDisplayOrder())
        .build())
      .toList();

    return ActualiteDetailsResponse.builder()
      .id(actualite.getId())
      .title(actualite.getTitle())
      .slug(actualite.getSlug())
      .content(actualite.getContent())
      .coverImageUrl(actualite.getCoverImageUrl())
      .galleryImages(publicImages)
      .galleryImagesAdmin(adminImages)
      .displayOrder(actualite.getDisplayOrder())
      .publishedAt(actualite.getPublishedAt())
      .build();
  }

  public List<ActualiteResponse> getAll() {
    return repository.findAllByOrderByDisplayOrderAsc()
      .stream()
      .map(this::toListDto)
      .toList();
  }

  /* ============================
     ✏️ CRÉATION / MISE À JOUR
     ============================ */

  @Transactional
  public ActualiteResponse create(
    String title,
    String content,
    Integer displayOrder,
    Boolean enabled,
    MultipartFile coverImage
  ) {

    if (coverImage == null || coverImage.isEmpty()) {
      throw new IllegalArgumentException("L’image de couverture est obligatoire");
    }

    Actualite actualite = Actualite.builder()
      .title(title)
      .slug(generateUniqueSlug(title, null))
      .content(content)
      .coverImageUrl(fileStorageService.storeActualiteCover(coverImage))
      .displayOrder(displayOrder)
      .enabled(false)
      .publishedAt(null)
      .build();

    Actualite saved = repository.save(actualite);

    if (Boolean.TRUE.equals(enabled)) {
      publish(saved);
      saved = repository.save(saved);
    }

    return toListDto(saved);
  }

  @Transactional
  public ActualiteResponse update(Long id, ActualiteUpdateRequest request) {

    Actualite actualite = get(id);

    if (request.title() != null) {
      actualite.setTitle(request.title());
      actualite.setSlug(generateUniqueSlug(request.title(), id));
    }

    if (request.content() != null) {
      actualite.setContent(request.content());
    }

    if (request.displayOrder() != null) {
      actualite.setDisplayOrder(request.displayOrder());
    }

    if (request.enabled() != null) {
      if (request.enabled()) publish(actualite);
      else unpublish(actualite);
    }

    return toListDto(repository.save(actualite));
  }

  @Transactional
  public ActualiteResponse updateCover(Long id, MultipartFile coverImage) {

    if (coverImage == null || coverImage.isEmpty()) {
      throw new IllegalArgumentException("Image de couverture manquante");
    }

    Actualite actualite = get(id);

    fileStorageService.delete(actualite.getCoverImageUrl());

    actualite.setCoverImageUrl(
      fileStorageService.storeActualiteCover(coverImage)
    );

    return toListDto(repository.save(actualite));
  }

  /* ============================
     🖼️ GALERIE
     ============================ */

  @Transactional
  public void addGalleryImages(Long actualiteId, List<MultipartFile> images) {

    Actualite actualite = get(actualiteId);

    int startOrder = imageRepository
      .findByActualiteIdOrderByDisplayOrderAsc(actualiteId)
      .size();

    for (MultipartFile file : images) {
      if (file.isEmpty()) continue;

      imageRepository.save(
        ActualiteImage.builder()
          .actualite(actualite)
          .imageUrl(fileStorageService.storeActualiteGalleryImage(file))
          .displayOrder(startOrder++)
          .build()
      );
    }
  }

  @Transactional
  public void replaceGalleryImages(Long actualiteId, List<MultipartFile> images) {

    imageRepository.findByActualiteIdOrderByDisplayOrderAsc(actualiteId)
      .forEach(img -> fileStorageService.delete(img.getImageUrl()));

    imageRepository.deleteByActualiteId(actualiteId);

    Actualite actualite = get(actualiteId);

    int order = 0;
    for (MultipartFile file : images) {
      if (file.isEmpty()) continue;

      imageRepository.save(
        ActualiteImage.builder()
          .actualite(actualite)
          .imageUrl(fileStorageService.storeActualiteGalleryImage(file))
          .displayOrder(order++)
          .build()
      );
    }
  }

  @Transactional
  public void deleteGalleryImage(Long imageId) {

    ActualiteImage image = imageRepository.findById(imageId)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Image introuvable"
      ));

    fileStorageService.delete(image.getImageUrl());
    imageRepository.delete(image);
  }

  /* ============================
     🗑️ SUPPRESSION ACTUALITÉ
     ============================ */

  @Transactional
  public void delete(Long id) {

    Actualite actualite = get(id);

    imageRepository.findByActualiteIdOrderByDisplayOrderAsc(id)
      .forEach(img -> fileStorageService.delete(img.getImageUrl()));

    imageRepository.deleteByActualiteId(id);

    fileStorageService.delete(actualite.getCoverImageUrl());

    historyRepository.deleteAll(
      historyRepository.findByActualiteIdOrdered(id)
    );

    repository.delete(actualite);
  }

  /* ============================
     📜 HISTORIQUE
     ============================ */

  public List<ActualitePublicationHistoryResponse> getPublicationHistory(Long id) {
    return historyRepository.findByActualiteIdOrdered(id)
      .stream()
      .map(h -> ActualitePublicationHistoryResponse.builder()
        .action(h.getAction().name())
        .actionDate(h.getActionDate())
        .build())
      .toList();
  }

  /* ============================
     🔒 MÉTIER
     ============================ */

  private void publish(Actualite actualite) {
    actualite.setEnabled(true);
    actualite.setPublishedAt(LocalDateTime.now());

    historyRepository.save(
      ActualitePublicationHistory.builder()
        .actualite(actualite)
        .action(PublicationAction.PUBLISHED)
        .actionDate(LocalDateTime.now())
        .build()
    );
  }

  private void unpublish(Actualite actualite) {
    actualite.setEnabled(false);
    actualite.setPublishedAt(null);

    historyRepository.save(
      ActualitePublicationHistory.builder()
        .actualite(actualite)
        .action(PublicationAction.UNPUBLISHED)
        .actionDate(LocalDateTime.now())
        .build()
    );
  }

  /* ============================
     🧩 UTILS
     ============================ */

  private Actualite get(Long id) {
    return repository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Actualité introuvable"
      ));
  }

  private ActualiteResponse toListDto(Actualite a) {
    return ActualiteResponse.builder()
      .id(a.getId())
      .title(a.getTitle())
      .slug(a.getSlug())
      .coverImageUrl(a.getCoverImageUrl())
      .publishedAt(a.getPublishedAt())
      .build();
  }

  private String generateUniqueSlug(String title, Long excludeId) {

    String base = org.apache.commons.lang3.StringUtils.stripAccents(title)
      .toLowerCase()
      .replaceAll("[^a-z0-9]+", "-")
      .replaceAll("(^-|-$)", "");

    String slug = base;
    int i = 1;

    while (
      excludeId == null
        ? repository.existsBySlug(slug)
        : repository.existsBySlugAndIdNot(slug, excludeId)
    ) {
      slug = base + "-" + i++;
    }

    return slug;
  }

  /* ============================
     🔁 RÉORDONNANCEMENT
     ============================ */

  @Transactional
  public void reorder(ActualiteReorderRequest request) {

    List<Long> ids = request.orderedIds();

    if (ids == null || ids.isEmpty()) {
      throw new IllegalArgumentException("Liste d’IDs vide");
    }

    int order = 0;

    for (Long id : ids) {
      Actualite actualite = repository.findById(id)
        .orElseThrow(() -> new RuntimeException(
          "Actualité introuvable : " + id
        ));

      actualite.setDisplayOrder(order++);
      repository.save(actualite);
    }
  }
}
