package com.school.api.banner.controller;

import com.school.api.banner.dto.BannerOrderRequest;
import com.school.api.banner.dto.BannerResponse;
import com.school.api.banner.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/banners")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class BannerAdminController {

  private final BannerService service;

  /* ============================
     📋 LISTE
     ============================ */

  @GetMapping
  public List<BannerResponse> all() {
    return service.getAll();
  }

  /* ============================
     ➕ CRÉATION (MULTIPART)
     ============================ */

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public BannerResponse create(
    @RequestParam String title,
    @RequestParam(required = false) String subtitle,
    @RequestParam(required = false) String subtitleAlt,
    @RequestParam Integer displayOrder,
    @RequestParam(required = false) Boolean enabled,

    // 🆕 DATES OPTIONNELLES
    @RequestParam(required = false) LocalDateTime startAt,
    @RequestParam(required = false) LocalDateTime endAt,

    @RequestParam MultipartFile media
  ) {
    return service.create(
      title,
      subtitle,
      subtitleAlt,
      media,
      displayOrder,
      enabled,
      startAt,
      endAt
    );
  }

  /* ============================
     ✏️ UPDATE
     ============================ */

  @PutMapping("/{id}")
  public BannerResponse update(
    @PathVariable Long id,
    @RequestBody com.school.api.banner.dto.BannerUpdateRequest request
  ) {
    return service.update(id, request);
  }

  /* ============================
     ✅ ACTIVER / DÉSACTIVER
     ============================ */

  @PutMapping("/{id}/enable")
  public BannerResponse enable(@PathVariable Long id) {
    return service.enable(id);
  }

  @PutMapping("/{id}/disable")
  public BannerResponse disable(@PathVariable Long id) {
    return service.disable(id);
  }

  /* ============================
     🔀 REORDER
     ============================ */

  @PutMapping("/reorder")
  public void reorder(@RequestBody List<BannerOrderRequest> orders) {
    service.reorder(orders);
  }

  /* ============================
     🗑️ SUPPRESSION
     ============================ */

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @GetMapping("/classified")
  public List<BannerResponse> classified() {
    return service.getAllClassified();
  }

}
