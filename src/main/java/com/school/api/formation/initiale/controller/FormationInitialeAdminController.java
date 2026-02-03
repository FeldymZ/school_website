package com.school.api.formation.initiale.controller;

import com.school.api.formation.initiale.dto.*;
import com.school.api.formation.initiale.entity.FormationInitialeLevel;
import com.school.api.formation.initiale.service.FormationInitialeService;
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

  /* =========================
     LISTE
     ========================= */
  @GetMapping
  public List<FormationInitialeResponse> all() {
    return service.getAll();
  }

  @GetMapping("/level/{level}")
  public List<FormationInitialeResponse> getByLevel(
    @PathVariable FormationInitialeLevel level
  ) {
    return service.getPublic(level);
  }

  /* =========================
     REDIRECTION ID → SLUG
     ========================= */
  @GetMapping("/{id}")
  public ResponseEntity<Void> redirectToSlug(@PathVariable Long id) {

    URI location = URI.create(
      "/api/admin/formations/initiale/slug/" +
        service.getSlugById(id)
    );

    return ResponseEntity
      .status(HttpStatus.MOVED_PERMANENTLY)
      .header(HttpHeaders.LOCATION, location.toString())
      .build();
  }

  @GetMapping("/slug/{slug}")
  public FormationInitialeDetailsResponse detailsBySlug(
    @PathVariable String slug
  ) {
    return service.getDetails(service.getIdBySlug(slug));
  }

  /* =========================
     CRÉATION
     ========================= */
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
    return service.create(
      name, description, level, displayOrder, enabled, coverImage, pdf
    );
  }

  /* =========================
     UPDATE
     ========================= */
  @PutMapping("/{id}")
  public FormationInitialeResponse update(
    @PathVariable Long id,
    @RequestBody FormationInitialeUpdateRequest request
  ) {
    return service.update(id, request);
  }

  /* =========================
     COVER
     ========================= */
  @PutMapping(
    value = "/{id}/cover",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public void updateCover(
    @PathVariable Long id,
    @RequestParam MultipartFile cover
  ) {
    service.updateCover(id, cover);
  }

  /* =========================
     PDF (AJOUT / REMPLACEMENT)
     ========================= */
  @PutMapping(
    value = "/{id}/pdf",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public void uploadPdf(
    @PathVariable Long id,
    @RequestParam MultipartFile pdf
  ) {
    service.updatePdf(id, pdf);
  }

  @DeleteMapping("/{id}/pdf")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public void deletePdf(@PathVariable Long id) {
    service.removePdf(id);
  }

  /* =========================
     GALERIE
     ========================= */
  @PostMapping(
    value = "/{id}/images",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public void addImages(
    @PathVariable Long id,
    @RequestParam("images") List<MultipartFile> images
  ) {
    service.addGalleryImages(id, images);
  }

  @DeleteMapping("/images/{imageId}")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public void deleteImage(@PathVariable Long imageId) {
    service.deleteGalleryImage(imageId);
  }

  @PutMapping("/{id}/images/reorder")
  public void reorderImages(
    @PathVariable Long id,
    @RequestBody List<FormationImageOrderRequest> orders
  ) {
    service.reorderGalleryImages(id, orders);
  }

  /* =========================
     SUPPRESSION FORMATION
     ========================= */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
