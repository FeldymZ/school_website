package com.school.api.banner.controller;

import com.school.api.banner.dto.BannerOrderRequest;
import com.school.api.banner.dto.BannerResponse;
import com.school.api.banner.dto.BannerUpdateRequest;
import com.school.api.banner.service.BannerService;
import com.school.api.auth.audit.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/banners")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class BannerAdminController {

  private final BannerService service;

  @AuditLog(action = "CONSULTATION_BANNERS")
  @GetMapping
  public List<BannerResponse> all() {
    return service.getAll();
  }

  @AuditLog(action = "CREATION_BANNER", target = "#title", failureAction = "CREATION_BANNER_ECHEC")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public BannerResponse create(
          @RequestParam String title,
          @RequestParam(required = false) String subtitle,
          @RequestParam(required = false) String subtitleAlt,
          @RequestParam Integer displayOrder,
          @RequestParam(required = false) Boolean enabled,
          @RequestParam(required = false) String startAt,
          @RequestParam(required = false) String endAt,
          @RequestParam(required = false) String buttonLabel,
          @RequestParam(required = false) String buttonUrl,
          @RequestParam MultipartFile media
  ) {
    return service.create(title, subtitle, subtitleAlt, media, displayOrder, enabled, startAt, endAt, buttonLabel, buttonUrl);
  }

  @AuditLog(action = "MODIFICATION_BANNER", target = "#id.toString()", failureAction = "MODIFICATION_BANNER_ECHEC")
  @PutMapping("/{id}")
  public BannerResponse update(
          @PathVariable Long id,
          @RequestBody BannerUpdateRequest request
  ) {
    return service.update(id, request);
  }

  @AuditLog(action = "ACTIVATION_BANNER", target = "#id.toString()")
  @PutMapping("/{id}/enable")
  public BannerResponse enable(@PathVariable Long id) {
    return service.enable(id);
  }

  @AuditLog(action = "DESACTIVATION_BANNER", target = "#id.toString()")
  @PutMapping("/{id}/disable")
  public BannerResponse disable(@PathVariable Long id) {
    return service.disable(id);
  }

  @AuditLog(action = "REORDONNANCEMENT_BANNERS")
  @PutMapping("/reorder")
  public void reorder(@RequestBody List<BannerOrderRequest> orders) {
    service.reorder(orders);
  }

  @AuditLog(action = "SUPPRESSION_BANNER", target = "#id.toString()", failureAction = "SUPPRESSION_BANNER_ECHEC")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @AuditLog(action = "CONSULTATION_BANNERS_CLASSES")
  @GetMapping("/classified")
  public List<BannerResponse> classified() {
    return service.getAllClassified();
  }
}