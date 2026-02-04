package com.school.api.formation.initiale.service;

import com.school.api.common.storage.FileStorageService;
import com.school.api.formation.initiale.dto.*;
import com.school.api.formation.initiale.entity.*;
import com.school.api.formation.initiale.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class FormationInitialeService {

  private final FormationInitialeRepository repository;
  private final FormationInitialeImageRepository imageRepository;
  private final FileStorageService fileStorageService;

  /* =========================
     🌍 PUBLIC
     ========================= */

  public List<FormationInitialeResponse> getAllPublic() {
    return repository
      .findByEnabledTrueOrderByDisplayOrderAsc()
      .stream()
      .map(this::toListDto)
      .toList();
  }

  public List<FormationInitialeResponse> getPublic(FormationInitialeLevel level) {
    return repository
      .findByEnabledTrueAndLevelOrderByDisplayOrderAsc(level)
      .stream()
      .map(this::toListDto)
      .toList();
  }

  public FormationInitialeDetailsResponse getPublicDetailsBySlug(String slug) {
    FormationInitiale formation = repository.findBySlug(slug)
      .filter(FormationInitiale::getEnabled)
      .orElseThrow(() -> new RuntimeException("Formation indisponible"));

    return buildDetails(formation);
  }

  /* =========================
     🔐 ADMIN
     ========================= */

  public List<FormationInitialeResponse> getAll() {
    return repository
      .findAllByOrderByDisplayOrderAsc()
      .stream()
      .map(this::toListDto)
      .toList();
  }

  public FormationInitialeDetailsResponse getDetails(Long id) {
    return buildDetails(get(id));
  }

  /* =========================
     ➕ CRÉATION
     ========================= */

  public FormationInitialeResponse create(
    String name,
    String description,
    FormationInitialeLevel level,
    Integer displayOrder,
    Boolean enabled,
    MultipartFile coverImage,
    MultipartFile pdf
  ) {

    if (coverImage == null || coverImage.isEmpty()) {
      throw new IllegalArgumentException("Image de couverture obligatoire");
    }

    String coverUrl = fileStorageService.storeFormationCover(coverImage);
    String pdfUrl = (pdf != null && !pdf.isEmpty())
      ? fileStorageService.storeFormationPdf(pdf)
      : null;

    FormationInitiale formation = FormationInitiale.builder()
      .name(name)
      .slug(generateUniqueSlug(name))
      .description(description)
      .level(level)
      .coverImageUrl(coverUrl)
      .pdfUrl(pdfUrl)
      .displayOrder(displayOrder)
      .enabled(enabled != null ? enabled : true)
      .build();

    return toListDto(repository.save(formation));
  }

  /* =========================
     ✏️ UPDATE
     ========================= */

  public FormationInitialeResponse update(
    Long id,
    FormationInitialeUpdateRequest request
  ) {

    FormationInitiale formation = get(id);

    if (request.name() != null &&
      !request.name().equals(formation.getName())) {
      formation.setName(request.name());
      formation.setSlug(generateUniqueSlug(request.name()));
    }

    if (request.description() != null)
      formation.setDescription(request.description());

    if (request.level() != null)
      formation.setLevel(request.level());

    if (request.displayOrder() != null)
      formation.setDisplayOrder(request.displayOrder());

    if (request.enabled() != null)
      formation.setEnabled(request.enabled());

    return toListDto(repository.save(formation));
  }

  /* =========================
     🖼️ COVER
     ========================= */

  public void updateCover(Long id, MultipartFile cover) {
    FormationInitiale formation = get(id);
    fileStorageService.deleteQuietly(formation.getCoverImageUrl());
    formation.setCoverImageUrl(
      fileStorageService.storeFormationCover(cover)
    );
    repository.save(formation);
  }

  /* =========================
     📄 PDF
     ========================= */

  public void updatePdf(Long id, MultipartFile pdf) {
    if (pdf == null || pdf.isEmpty()) {
      throw new IllegalArgumentException("PDF invalide");
    }

    FormationInitiale formation = get(id);
    fileStorageService.deleteQuietly(formation.getPdfUrl());
    formation.setPdfUrl(
      fileStorageService.storeFormationPdf(pdf)
    );
    repository.save(formation);
  }

  public void removePdf(Long id) {
    FormationInitiale formation = get(id);
    fileStorageService.deleteQuietly(formation.getPdfUrl());
    formation.setPdfUrl(null);
    repository.save(formation);
  }

  /* =========================
     🖼️ GALERIE
     ========================= */

  public void addGalleryImages(Long formationId, List<MultipartFile> images) {

    if (images == null || images.isEmpty()) return;

    FormationInitiale formation = get(formationId);
    int order = imageRepository
      .findByFormationIdOrderByDisplayOrderAsc(formationId)
      .size();

    for (MultipartFile file : images) {
      if (file.isEmpty()) continue;

      imageRepository.save(
        FormationInitialeImage.builder()
          .formation(formation)
          .imageUrl(fileStorageService.storeFormationGalleryImage(file))
          .displayOrder(order++)
          .build()
      );
    }
  }

  @Transactional
  public void deleteGalleryImage(Long imageId) {
    FormationInitialeImage image = imageRepository.findById(imageId)
      .orElseThrow(() -> new RuntimeException("Image introuvable"));

    fileStorageService.deleteQuietly(image.getImageUrl());
    imageRepository.delete(image);
  }

  @Transactional
  public void reorderGalleryImages(
    Long formationId,
    List<FormationImageOrderRequest> orders
  ) {

    FormationInitiale formation = get(formationId);

    for (FormationImageOrderRequest item : orders) {
      FormationInitialeImage image = imageRepository.findById(item.id())
        .orElseThrow(() -> new RuntimeException("Image introuvable"));

      if (!image.getFormation().getId().equals(formation.getId())) {
        throw new IllegalArgumentException(
          "Image invalide pour cette formation"
        );
      }

      image.setDisplayOrder(item.displayOrder());
    }
  }

  /* =========================
     🗑️ SUPPRESSION
     ========================= */

  @Transactional
  public void delete(Long id) {

    imageRepository.findByFormationIdOrderByDisplayOrderAsc(id)
      .forEach(img ->
        fileStorageService.deleteQuietly(img.getImageUrl())
      );

    imageRepository.deleteByFormationId(id);

    FormationInitiale formation = get(id);
    fileStorageService.deleteQuietly(formation.getCoverImageUrl());
    fileStorageService.deleteQuietly(formation.getPdfUrl());

    repository.delete(formation);
  }

  /* =========================
     🔁 SLUG ↔ ID
     ========================= */

  public String getSlugById(Long id) {
    return get(id).getSlug();
  }

  public Long getIdBySlug(String slug) {
    return repository.findBySlug(slug)
      .orElseThrow(() -> new RuntimeException("Formation introuvable"))
      .getId();
  }

  /* =========================
     🧩 UTILS
     ========================= */

  private FormationInitiale get(Long id) {
    return repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Formation introuvable"));
  }

  /**
   * 🔥 MÉTHODE CLÉ — ALIGNÉE AVEC LE FRONTEND
   */
  private FormationInitialeDetailsResponse buildDetails(FormationInitiale f) {
    return FormationInitialeDetailsResponse.builder()
      .id(f.getId())
      .title(f.getLevel().getLabel() + " " + f.getName())
      .slug(f.getSlug())
      .description(f.getDescription())
      .coverImageUrl(f.getCoverImageUrl())
      .galleryImages(
        imageRepository
          .findByFormationIdOrderByDisplayOrderAsc(f.getId())
          .stream()
          .map(img -> FormationGalleryImageResponse.builder()
            .id(img.getId())
            .url(img.getImageUrl()) // ✅ url simple
            .displayOrder(img.getDisplayOrder())
            .build()
          )
          .toList()
      )
      .pdfUrl(f.getPdfUrl())
      .level(f.getLevel())
      .build();
  }

  private FormationInitialeResponse toListDto(FormationInitiale f) {
    return FormationInitialeResponse.builder()
      .id(f.getId())
      .title(f.getLevel().getLabel() + " " + f.getName())
      .slug(f.getSlug())
      .coverImageUrl(f.getCoverImageUrl())
      .level(f.getLevel())
      .build();
  }

  private String generateUniqueSlug(String input) {
    String base = Normalizer.normalize(input, Normalizer.Form.NFD)
      .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
      .toLowerCase(Locale.ROOT)
      .replaceAll("[^a-z0-9]+", "-")
      .replaceAll("(^-|-$)", "");

    String slug = base;
    int i = 1;
    while (repository.existsBySlug(slug)) {
      slug = base + "-" + i++;
    }
    return slug;
  }
}
