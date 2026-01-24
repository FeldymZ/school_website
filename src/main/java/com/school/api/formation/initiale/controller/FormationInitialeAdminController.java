package com.school.api.formation.initiale.controller;

import com.school.api.formation.initiale.dto.FormationImageOrderRequest;
import com.school.api.formation.initiale.dto.FormationInitialeDetailsResponse;
import com.school.api.formation.initiale.dto.FormationInitialeResponse;
import com.school.api.formation.initiale.dto.FormationInitialeUpdateRequest;
import com.school.api.formation.initiale.entity.FormationInitialeLevel;
import com.school.api.formation.initiale.service.FormationInitialeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/formations/initiale")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class FormationInitialeAdminController {

  private final FormationInitialeService service;

  /* ============================
     📋 LISTE ADMIN
     ============================ */
  @GetMapping
  public List<FormationInitialeResponse> all() {
    return service.getAll();
  }

  /* ============================
     📋 LISTE PAR NIVEAU (ADMIN)
     ============================ */
  @GetMapping("/level/{level}")
  public List<FormationInitialeResponse> getByLevel(
    @PathVariable FormationInitialeLevel level
  ) {
    return service.getPublic(level);
  }

  /* ============================
     🔎 DÉTAILS (ADMIN)
     ============================ */
  @GetMapping("/{id}")
  public FormationInitialeDetailsResponse details(@PathVariable Long id) {
    return service.getDetails(id);
  }

  /* ============================
     ➕ CRÉATION
     ============================ */
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

  /* ============================
      MODIFIER INFOS
     ============================ */
  @PutMapping("/{id}")
  public FormationInitialeResponse update(
    @PathVariable Long id,
    @RequestBody FormationInitialeUpdateRequest request
  ) {
    return service.update(id, request);
  }

  /* ============================
      COVER
     ============================ */
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

  /* ============================
      GALERIE
     ============================ */
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

  /* ============================
      PDF
     ============================ */
  @DeleteMapping("/{id}/pdf")
  public void deletePdf(@PathVariable Long id) {
    service.removePdf(id);
  }

  /* ============================
      SUPPRESSION
     ============================ */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
