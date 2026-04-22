package com.school.api.formation.initiale.controller;

import com.school.api.formation.initiale.dto.*;
import com.school.api.formation.initiale.entity.FormationInitialeLevel;
import com.school.api.formation.initiale.service.FormationInitialeService;
import com.school.api.auth.audit.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/formations/initiale")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class FormationInitialeAdminController {

  private final FormationInitialeService service;

  /* ========================= LISTE ========================= */

  @AuditLog(action = "CONSULTATION_FORMATIONS_INITIALES")
  @GetMapping
  public List<FormationInitialeResponse> all() {
    return service.getAll();
  }

  @AuditLog(action = "CONSULTATION_FORMATIONS_INITIALES_PAR_NIVEAU", target = "#level.toString()")
  @GetMapping("/level/{level}")
  public List<FormationInitialeResponse> getByLevel(
          @PathVariable FormationInitialeLevel level
  ) {
    return service.getPublic(level);
  }

  /* ========================= REDIRECTION ID → SLUG ========================= */

  @GetMapping("/{id}")
  public ResponseEntity<Void> redirectToSlug(@PathVariable Long id) {
    URI location = URI.create(
            "/api/admin/formations/initiale/slug/" + service.getSlugById(id)
    );
    return ResponseEntity
            .status(HttpStatus.MOVED_PERMANENTLY)
            .header(HttpHeaders.LOCATION, location.toString())
            .build();
  }

  @AuditLog(action = "CONSULTATION_FORMATION_INITIALE", target = "#slug")
  @GetMapping("/slug/{slug}")
  public FormationInitialeDetailsResponse detailsBySlug(@PathVariable String slug) {
    return service.getDetails(service.getIdBySlug(slug));
  }

  /* ========================= CRÉATION ========================= */

  @AuditLog(action = "CREATION_FORMATION_INITIALE", target = "#name", failureAction = "CREATION_FORMATION_INITIALE_ECHEC")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public FormationInitialeResponse create(
          @RequestParam String name,
          @RequestParam(required = false) String description,
          @RequestParam FormationInitialeLevel level,
          @RequestParam Integer displayOrder,
          @RequestParam(required = false) Boolean enabled,
          @RequestParam MultipartFile coverImage,
          @RequestParam(required = false) MultipartFile pdf
  ) {
    return service.create(name, description, level, displayOrder, enabled, coverImage, pdf);
  }

  /* ========================= UPDATE ========================= */

  @AuditLog(action = "MODIFICATION_FORMATION_INITIALE", target = "#id.toString()", failureAction = "MODIFICATION_FORMATION_INITIALE_ECHEC")
  @PutMapping("/{id}")
  public FormationInitialeResponse update(
          @PathVariable Long id,
          @RequestBody FormationInitialeUpdateRequest request
  ) {
    return service.update(id, request);
  }

  /* ========================= COVER ========================= */

  @AuditLog(action = "MODIFICATION_COUVERTURE_FORMATION_INITIALE", target = "#id.toString()")
  @PutMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void updateCover(
          @PathVariable Long id,
          @RequestParam MultipartFile cover
  ) {
    service.updateCover(id, cover);
  }

  /* ========================= PDF ========================= */

  @AuditLog(action = "UPLOAD_PDF_FORMATION_INITIALE", target = "#id.toString()", failureAction = "UPLOAD_PDF_ECHEC")
  @PutMapping(value = "/{id}/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public void uploadPdf(
          @PathVariable Long id,
          @RequestParam MultipartFile pdf
  ) {
    service.updatePdf(id, pdf);
  }

  @AuditLog(action = "SUPPRESSION_PDF_FORMATION_INITIALE", target = "#id.toString()", failureAction = "SUPPRESSION_PDF_ECHEC")
  @DeleteMapping("/{id}/pdf")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public void deletePdf(@PathVariable Long id) {
    service.removePdf(id);
  }

  /* ========================= GALERIE ========================= */

  @AuditLog(action = "AJOUT_IMAGES_FORMATION_INITIALE", target = "#id.toString()")
  @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void addImages(
          @PathVariable Long id,
          @RequestParam("images") List<MultipartFile> images
  ) {
    service.addGalleryImages(id, images);
  }

  @AuditLog(action = "SUPPRESSION_IMAGE_FORMATION_INITIALE", target = "#imageId.toString()", failureAction = "SUPPRESSION_IMAGE_ECHEC")
  @DeleteMapping("/images/{imageId}")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public void deleteImage(@PathVariable Long imageId) {
    service.deleteGalleryImage(imageId);
  }

  @AuditLog(action = "REORDONNANCEMENT_IMAGES_FORMATION_INITIALE", target = "#id.toString()")
  @PutMapping("/{id}/images/reorder")
  public void reorderImages(
          @PathVariable Long id,
          @RequestBody List<FormationImageOrderRequest> orders
  ) {
    service.reorderGalleryImages(id, orders);
  }

  /* ========================= SUPPRESSION ========================= */

  @AuditLog(action = "SUPPRESSION_FORMATION_INITIALE", target = "#id.toString()", failureAction = "SUPPRESSION_FORMATION_INITIALE_ECHEC")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}