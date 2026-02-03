package com.school.api.actualite.service;

import com.school.api.actualite.dto.*;
import com.school.api.actualite.entity.*;
import com.school.api.actualite.repository.*;
import com.school.api.common.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

  /** Liste publique */
  public List<ActualiteResponse> getPublic() {
    return repository.findPublicVisible()
      .stream()
      .map(this::toListDto)
      .toList();
  }

  /** Détails publics par slug (URL canonique) */
  public ActualiteDetailsResponse getDetailsBySlug(String slug) {

    Actualite actualite = repository.findBySlug(slug)
      .orElseThrow(() -> new RuntimeException("Actualité introuvable"));

    return getDetails(actualite.getId());
  }

  /** Récupération du slug pour redirection ID → slug */
  public String getSlugById(Long id) {
    return repository.findById(id)
      .map(Actualite::getSlug)
      .orElseThrow(() -> new RuntimeException("Actualité introuvable"));
  }

  /* ============================
     🔐 ADMIN / PUBLIC
     ============================ */

  /** Détails complets (admin & public) */
  public ActualiteDetailsResponse getDetails(Long id) {

    Actualite actualite = get(id);

    List<ActualiteImage> images =
      imageRepository.findByActualiteIdOrderByDisplayOrderAsc(id);

    // 🌍 PUBLIC → URL uniquement
    List<String> publicImages = images.stream()
      .map(ActualiteImage::getImageUrl)
      .toList();

    // 🔐 ADMIN → ID + URL
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

  /** Liste admin */
  public List<ActualiteResponse> getAll() {
    return repository.findAllByOrderByDisplayOrderAsc()
      .stream()
      .map(this::toListDto)
      .toList();
  }

  /* ============================
     ✏️ CRÉATION / MISE À JOUR
     ============================ */

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
      .slug(generateSlug(title))
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

  public ActualiteResponse update(Long id, ActualiteUpdateRequest request) {

    Actualite actualite = get(id);

    if (request.title() != null) {
      actualite.setTitle(request.title());
      actualite.setSlug(generateSlug(request.title()));
    }

    if (request.content() != null) {
      actualite.setContent(request.content());
    }

    if (request.displayOrder() != null) {
      actualite.setDisplayOrder(request.displayOrder());
    }

    if (request.enabled() != null) {
      if (request.enabled()) {
        publish(actualite);
      } else {
        unpublish(actualite);
      }
    }

    return toListDto(repository.save(actualite));
  }

  public ActualiteResponse updateCover(Long id, MultipartFile coverImage) {

    if (coverImage == null || coverImage.isEmpty()) {
      throw new IllegalArgumentException("Image de couverture manquante");
    }

    Actualite actualite = get(id);
    actualite.setCoverImageUrl(
      fileStorageService.storeActualiteCover(coverImage)
    );

    return toListDto(repository.save(actualite));
  }

  /* ============================
     🖼️ GALERIE
     ============================ */

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

  public void replaceGalleryImages(Long actualiteId, List<MultipartFile> images) {

    Actualite actualite = get(actualiteId);
    imageRepository.deleteByActualiteId(actualiteId);

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

  public void deleteGalleryImage(Long imageId) {

    ActualiteImage image = imageRepository.findById(imageId)
      .orElseThrow(() -> new RuntimeException("Image introuvable"));

    fileStorageService.delete(image.getImageUrl());
    imageRepository.delete(image);
  }

  /* ============================
     🗑️ SUPPRESSION ACTUALITÉ
     ============================ */

  public void delete(Long id) {
    imageRepository.deleteByActualiteId(id);
    historyRepository.deleteAll(
      historyRepository.findByActualiteIdOrdered(id)
    );
    repository.delete(get(id));
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
      .orElseThrow(() -> new RuntimeException("Actualité introuvable"));
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

  private String generateSlug(String title) {

    String base = org.apache.commons.lang3.StringUtils.stripAccents(title)
      .toLowerCase()
      .replaceAll("[^a-z0-9]+", "-")
      .replaceAll("(^-|-$)", "");

    String slug = base;
    int i = 1;

    while (repository.existsBySlug(slug)) {
      slug = base + "-" + i++;
    }

    return slug;
  }

  /* ============================
     🔁 RÉORDONNANCEMENT
     ============================ */

  public void reorder(ActualiteReorderRequest request) {

    List<Long> ids = request.orderedIds();

    if (ids == null || ids.isEmpty()) {
      throw new IllegalArgumentException("Liste d’IDs vide");
    }

    int order = 0;

    for (Long id : ids) {
      Actualite actualite = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Actualité introuvable : " + id));

      actualite.setDisplayOrder(order++);
      repository.save(actualite);
    }
  }
}
