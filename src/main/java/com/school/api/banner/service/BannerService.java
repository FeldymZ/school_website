package com.school.api.banner.service;

import com.school.api.banner.dto.BannerOrderRequest;
import com.school.api.banner.dto.BannerRequest;
import com.school.api.banner.dto.BannerResponse;
import com.school.api.banner.dto.BannerUpdateRequest;
import com.school.api.banner.entity.Banner;
import com.school.api.banner.entity.MediaType;
import com.school.api.banner.repository.BannerRepository;
import com.school.api.common.storage.FileStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import com.school.api.banner.entity.BannerStatus;

@Service
@RequiredArgsConstructor
public class BannerService {

  private final BannerRepository repository;
  private final FileStorageService fileStorageService;


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


  /* ============================
     🌍 PUBLIC
     ============================ */

  public List<BannerResponse> getPublicBanners() {
    return repository.findActiveBanners(LocalDateTime.now())
      .stream()
      .map(this::toDto)
      .toList();
  }

  /* ============================
     🔐 ADMIN
     ============================ */

  public List<BannerResponse> getAll() {
    return repository.findAllByOrderByDisplayOrderAsc()
      .stream()
      .map(this::toDto)
      .toList();
  }

  public BannerResponse create(
    String title,
    String subtitle,
    String subtitleAlt,
    MultipartFile media,
    Integer displayOrder,
    Boolean enabled,
    LocalDateTime startAt,
    LocalDateTime endAt
  ) {

    if (media == null || media.isEmpty()) {
      throw new IllegalArgumentException("Fichier média requis");
    }

    if (repository.existsByDisplayOrder(displayOrder)) {
      throw new IllegalArgumentException(
        "La position " + displayOrder + " est déjà utilisée"
      );
    }

    // 🆕 Validation dates (OPTIONNELLES)
    if (
      startAt != null &&
        endAt != null &&
        startAt.isAfter(endAt)
    ) {
      throw new IllegalArgumentException(
        "La date de début doit être antérieure à la date de fin"
      );
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
      // 🆕 dates à la création
      .startAt(startAt)
      .endAt(endAt)
      .build();

    return toDto(repository.save(banner));
  }


  public BannerResponse update(Long id, BannerUpdateRequest request)
  {

    Banner banner = get(id);

    if (request.title() != null) banner.setTitle(request.title());
    if (request.subtitle() != null) banner.setSubtitle(request.subtitle());
    if (request.subtitleAlt() != null) banner.setSubtitleAlt(request.subtitleAlt());

    if (
      request.displayOrder() != null &&
        !request.displayOrder().equals(banner.getDisplayOrder()) &&
        repository.existsByDisplayOrder(request.displayOrder())
    ) {
      throw new IllegalArgumentException(
        "La position " + request.displayOrder() + " est déjà utilisée"
      );
    }

    if (request.displayOrder() != null) {
      banner.setDisplayOrder(request.displayOrder());
    }

    if (request.enabled() != null) {
      banner.setEnabled(request.enabled());
    }

    // 🆕 Dates OPTIONNELLES
    if (request.startAt() != null) banner.setStartAt(request.startAt());
    if (request.endAt() != null) banner.setEndAt(request.endAt());

    // Validation simple
    if (
      banner.getStartAt() != null &&
        banner.getEndAt() != null &&
        banner.getStartAt().isAfter(banner.getEndAt())
    ) {
      throw new IllegalArgumentException(
        "La date de début doit être antérieure à la date de fin"
      );
    }

    return toDto(repository.save(banner));
  }

  public BannerResponse enable(Long id) {
    Banner banner = get(id);
    banner.setEnabled(true);
    return toDto(repository.save(banner));
  }

  public BannerResponse disable(Long id) {
    Banner banner = get(id);
    banner.setEnabled(false);
    return toDto(repository.save(banner));
  }

  public void delete(Long id) {
    repository.delete(get(id));
  }

  /* ============================
     🔀 DRAG & DROP
     ============================ */

  @Transactional
  public void reorder(List<BannerOrderRequest> orders) {

    long distinctCount = orders.stream()
      .map(BannerOrderRequest::displayOrder)
      .distinct()
      .count();

    if (distinctCount != orders.size()) {
      throw new IllegalArgumentException(
        "Deux bannières ne peuvent pas avoir la même position"
      );
    }

    for (BannerOrderRequest item : orders) {
      get(item.id()).setDisplayOrder(item.displayOrder());
    }
  }

  /* ============================
     🧩 UTILS
     ============================ */

  private Banner get(Long id) {
    return repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Banner introuvable"));
  }

  private MediaType resolveMediaType(String contentType) {
    if (contentType.startsWith("image/")) return MediaType.IMAGE;
    if (contentType.startsWith("video/")) return MediaType.VIDEO;
    throw new IllegalArgumentException("Type de fichier non supporté");
  }

  public List<BannerResponse> getAllClassified() {
    return repository.findAllByOrderByDisplayOrderAsc()
      .stream()
      .map(this::toDto)
      .toList();
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
      .status(resolveStatus(banner))
      .build();
  }


}
