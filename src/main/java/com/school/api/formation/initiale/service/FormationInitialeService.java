package com.school.api.formation.initiale.service;

import com.school.api.common.storage.FileStorageService;
import com.school.api.formation.initiale.dto.FormationImageOrderRequest;
import com.school.api.formation.initiale.dto.FormationInitialeDetailsResponse;
import com.school.api.formation.initiale.dto.FormationInitialeResponse;
import com.school.api.formation.initiale.dto.FormationInitialeUpdateRequest;
import com.school.api.formation.initiale.entity.FormationInitiale;
import com.school.api.formation.initiale.entity.FormationInitialeImage;
import com.school.api.formation.initiale.entity.FormationInitialeLevel;
import com.school.api.formation.initiale.repository.FormationInitialeImageRepository;
import com.school.api.formation.initiale.repository.FormationInitialeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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

  /* =====================================================
     🌍 PUBLIC
     ===================================================== */

  public List<FormationInitialeResponse> getAllPublic() {
    return repository
      .findByEnabledTrueOrderByDisplayOrderAsc()
      .stream()
      .map(this::toListDto)
      .toList();
  }

  public List<FormationInitialeResponse> getPublic(
    FormationInitialeLevel level
  ) {
    return repository
      .findByEnabledTrueAndLevelOrderByDisplayOrderAsc(level)
      .stream()
      .map(this::toListDto)
      .toList();
  }

  public FormationInitialeDetailsResponse getPublicDetailsBySlug(
    String slug
  ) {
    FormationInitiale formation = repository.findBySlug(slug)
      .filter(FormationInitiale::getEnabled)
      .orElseThrow(() -> new RuntimeException("Formation indisponible"));

    return buildDetails(formation);
  }

  /* =====================================================
     🔐 ADMIN
     ===================================================== */

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

  public void updateCover(Long id, MultipartFile cover) {

    if (cover == null || cover.isEmpty()) {
      throw new IllegalArgumentException("Cover invalide");
    }

    FormationInitiale formation = get(id);
    formation.setCoverImageUrl(
      fileStorageService.storeFormationCover(cover)
    );
    repository.save(formation);
  }

  /* =====================================================
     🖼️ GALERIE
     ===================================================== */

  public void addGalleryImages(
    Long formationId,
    List<MultipartFile> images
  ) {

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
          .imageUrl(
            fileStorageService.storeFormationGalleryImage(file)
          )
          .displayOrder(order++)
          .build()
      );
    }
  }

  /**
   * ❌ SUPPRESSION IMAGE — SUPERADMIN UNIQUEMENT
   */
  public void deleteGalleryImage(Long imageId) {

    var auth = SecurityContextHolder.getContext().getAuthentication();

    if (
      auth == null ||
      auth.getAuthorities().stream().noneMatch(
        a -> a.getAuthority().equals("ROLE_SUPERADMIN")
      )
    ) {
      throw new SecurityException(
        "Suppression réservée au SUPERADMIN"
      );
    }

    imageRepository.delete(
      imageRepository.findById(imageId)
        .orElseThrow(() -> new RuntimeException("Image introuvable"))
    );
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

  /* =====================================================
     📄 PDF
     ===================================================== */

  public void removePdf(Long id) {
    FormationInitiale formation = get(id);
    formation.setPdfUrl(null);
    repository.save(formation);
  }

  /* =====================================================
     🗑️ SUPPRESSION
     ===================================================== */

  @Transactional
  public void delete(Long id) {
    imageRepository.deleteByFormationId(id);
    repository.delete(get(id));
  }

  /* =====================================================
     🔁 SLUG ↔ ID
     ===================================================== */

  public String getSlugById(Long id) {
    return get(id).getSlug();
  }

  public Long getIdBySlug(String slug) {
    return repository.findBySlug(slug)
      .orElseThrow(() -> new RuntimeException("Formation introuvable"))
      .getId();
  }

  /* =====================================================
     🧩 UTILS INTERNES
     ===================================================== */

  private FormationInitiale get(Long id) {
    return repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Formation introuvable"));
  }

  private FormationInitialeDetailsResponse buildDetails(
    FormationInitiale f
  ) {

    List<String> galleryImages = imageRepository
      .findByFormationIdOrderByDisplayOrderAsc(f.getId())
      .stream()
      .map(FormationInitialeImage::getImageUrl)
      .toList();

    return FormationInitialeDetailsResponse.builder()
      .id(f.getId())
      .title(buildTitle(f))
      .slug(f.getSlug())
      .description(f.getDescription())
      .coverImageUrl(f.getCoverImageUrl())
      .galleryImages(galleryImages)
      .pdfUrl(f.getPdfUrl())
      .level(f.getLevel())
      .build();
  }

  private FormationInitialeResponse toListDto(FormationInitiale f) {
    return FormationInitialeResponse.builder()
      .id(f.getId())
      .title(buildTitle(f))
      .slug(f.getSlug())
      .coverImageUrl(f.getCoverImageUrl())
      .level(f.getLevel())
      .build();
  }

  private String buildTitle(FormationInitiale f) {
    return f.getLevel().getLabel() + " " + f.getName();
  }

  private String generateUniqueSlug(String input) {

    String baseSlug = Normalizer.normalize(input, Normalizer.Form.NFD)
      .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
      .toLowerCase(Locale.ROOT)
      .replaceAll("[^a-z0-9]+", "-")
      .replaceAll("(^-|-$)", "");

    String slug = baseSlug;
    int counter = 1;

    while (repository.existsBySlug(slug)) {
      slug = baseSlug + "-" + counter++;
    }

    return slug;
  }
}
