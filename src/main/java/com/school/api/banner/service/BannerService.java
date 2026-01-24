package com.school.api.banner.service;

import com.school.api.banner.dto.BannerOrderRequest;
import com.school.api.banner.dto.BannerResponse;
import com.school.api.banner.dto.BannerUpdateRequest;
import com.school.api.banner.entity.Banner;
import com.school.api.banner.entity.BannerStatus;
import com.school.api.banner.entity.MediaType;
import com.school.api.banner.repository.BannerRepository;
import com.school.api.common.storage.FileStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {

  private final BannerRepository repository;
  private final FileStorageService fileStorageService;

  /* =====================================================
     🌍 PUBLIC
     ===================================================== */

  public List<BannerResponse> getPublicBanners() {
    return repository.findPublic(LocalDateTime.now())
      .stream()
      .map(this::toDto)
      .toList();
  }

  /* =====================================================
     🔐 ADMIN
     ===================================================== */

  public List<BannerResponse> getAll() {
    return repository.findAllByOrderByDisplayOrderAsc()
      .stream()
      .map(this::toDto)
      .toList();
  }

  public List<BannerResponse> getAllClassified() {
    return repository.findAllByOrderByDisplayOrderAsc()
      .stream()
      .map(this::toDto)
      .toList();
  }

  /* =====================================================
     ➕ CREATE (MULTIPART + BOUTON)
     ===================================================== */

  @Transactional
  public BannerResponse create(
    String title,
    String subtitle,
    String subtitleAlt,
    MultipartFile media,
    Integer displayOrder,
    Boolean enabled,
    String startAt,
    String endAt,
    String buttonLabel,
    String buttonUrl
  ) {

    if (media == null || media.isEmpty()) {
      throw new IllegalArgumentException("Fichier média requis");
    }

    validateButtonUrl(buttonUrl);

    LocalDateTime start = parseDate(startAt);
    LocalDateTime end = parseDate(endAt);

    if (start != null && end != null && start.isAfter(end)) {
      throw new IllegalArgumentException("Dates invalides");
    }

    if (repository.existsByDisplayOrder(displayOrder)) {
      repository.shiftDownFrom(displayOrder);
    }

    MediaType mediaType = resolveMediaType(media.getContentType());
    String mediaUrl = fileStorageService.storeBannerMedia(media, mediaType);

    Banner banner = Banner.builder()
      .title(title)
      .subtitle(subtitle)
      .subtitleAlt(subtitleAlt)
      .mediaUrl(mediaUrl)
      .mediaType(mediaType)
      .displayOrder(displayOrder)
      .enabled(enabled != null ? enabled : true)
      .startAt(start)
      .endAt(end)

      // bouton optionnel
      .buttonUrl(buttonUrl)
      .buttonLabel(
        buttonUrl != null
          ? (buttonLabel != null ? buttonLabel : "En savoir plus")
          : null
      )

      .build();

    return toDto(repository.save(banner));
  }

  /* =====================================================
     ✏️ UPDATE
     ===================================================== */

  @Transactional
  public BannerResponse update(Long id, BannerUpdateRequest request) {

    Banner banner = get(id);

    if (request.buttonUrl() != null) {
      validateButtonUrl(request.buttonUrl());
    }

    Integer oldOrder = banner.getDisplayOrder();
    Integer newOrder = request.displayOrder();

    if (newOrder != null && !newOrder.equals(oldOrder)) {
      if (newOrder < oldOrder) {
        repository.shiftDownFrom(newOrder);
      } else {
        repository.shiftUpBetween(oldOrder, newOrder);
      }
      banner.setDisplayOrder(newOrder);
    }

    if (request.title() != null) banner.setTitle(request.title());
    if (request.subtitle() != null) banner.setSubtitle(request.subtitle());
    if (request.subtitleAlt() != null) banner.setSubtitleAlt(request.subtitleAlt());
    if (request.enabled() != null) banner.setEnabled(request.enabled());
    if (request.startAt() != null) banner.setStartAt(request.startAt());
    if (request.endAt() != null) banner.setEndAt(request.endAt());

    // bouton
    if (request.buttonUrl() != null) {
      banner.setButtonUrl(request.buttonUrl());
      banner.setButtonLabel(
        request.buttonLabel() != null
          ? request.buttonLabel()
          : "En savoir plus"
      );
    }

    if (request.buttonUrl() == null && request.buttonLabel() == null) {
      banner.setButtonUrl(null);
      banner.setButtonLabel(null);
    }

    return toDto(repository.save(banner));
  }

  /* =====================================================
     ✅ ENABLE / DISABLE
     ===================================================== */

  @Transactional
  public BannerResponse enable(Long id) {
    Banner banner = get(id);
    banner.setEnabled(true);
    return toDto(repository.save(banner));
  }

  @Transactional
  public BannerResponse disable(Long id) {
    Banner banner = get(id);
    banner.setEnabled(false);
    return toDto(repository.save(banner));
  }

  /* =====================================================
     🗑️ DELETE
     ===================================================== */

  @Transactional
  public void delete(Long id) {
    Banner banner = get(id);
    repository.delete(banner);
    repository.compactAfterDelete(banner.getDisplayOrder());
  }

  /* =====================================================
     🔀 REORDER
     ===================================================== */

  @Transactional
  public void reorder(List<BannerOrderRequest> orders) {

    long distinct = orders.stream()
      .map(BannerOrderRequest::displayOrder)
      .distinct()
      .count();

    if (distinct != orders.size()) {
      throw new IllegalArgumentException("Positions en doublon");
    }

    orders.forEach(o ->
      get(o.id()).setDisplayOrder(o.displayOrder())
    );
  }

  /* =====================================================
     🧠 STATUS
     ===================================================== */

  private BannerStatus resolveStatus(Banner banner) {

    if (Boolean.FALSE.equals(banner.getEnabled())) {
      return BannerStatus.DISABLED;
    }

    LocalDateTime now = LocalDateTime.now();

    if (banner.getStartAt() != null && banner.getStartAt().isAfter(now)) {
      return BannerStatus.PROGRAMMED;
    }

    if (banner.getEndAt() != null && banner.getEndAt().isBefore(now)) {
      return BannerStatus.EXPIRED;
    }

    return BannerStatus.ACTIVE;
  }

  /* =====================================================
     🔐 VALIDATION URL
     ===================================================== */

  private void validateButtonUrl(String url) {
    if (url == null || url.isBlank()) return;

    if (
      url.startsWith("/") ||
        url.startsWith("http://") ||
        url.startsWith("https://")
    ) {
      return;
    }

    throw new IllegalArgumentException("URL de redirection invalide");
  }

  /* =====================================================
     🧩 UTILS
     ===================================================== */

  private Banner get(Long id) {
    return repository.findById(id)
      .orElseThrow(() ->
        new IllegalArgumentException("Banner introuvable")
      );
  }

  private LocalDateTime parseDate(String value) {
    return value != null ? LocalDateTime.parse(value) : null;
  }

  private MediaType resolveMediaType(String contentType) {
    if (contentType == null) {
      throw new IllegalArgumentException("Type de fichier inconnu");
    }
    if (contentType.startsWith("image/")) return MediaType.IMAGE;
    if (contentType.startsWith("video/")) return MediaType.VIDEO;
    throw new IllegalArgumentException("Type non supporté");
  }

  private BannerResponse toDto(Banner banner) {
    return BannerResponse.builder()
      .id(banner.getId())
      .title(banner.getTitle())
      .subtitle(banner.getSubtitle())
      .subtitleAlt(banner.getSubtitleAlt())
      .mediaUrl(banner.getMediaUrl())
      .mediaType(banner.getMediaType())
      .displayOrder(banner.getDisplayOrder())
      .enabled(banner.getEnabled())
      .startAt(banner.getStartAt())
      .endAt(banner.getEndAt())
      .buttonLabel(banner.getButtonLabel())
      .buttonUrl(banner.getButtonUrl())
      .status(resolveStatus(banner))
      .build();
  }
}
