package com.school.api.actualite.controller;

import com.school.api.actualite.dto.*;
import com.school.api.actualite.service.ActualiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/actualites")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class ActualiteAdminController {

  private final ActualiteService service;

  /* =========================
     LISTE DES ACTUALITÉS
     ========================= */
  @GetMapping
  public List<ActualiteResponse> all() {
    return service.getAll();
  }

  /* =========================
     DÉTAIL D’UNE ACTUALITÉ
     ========================= */
  @GetMapping("/{id}")
  public ActualiteDetailsResponse getDetails(@PathVariable Long id) {
    return service.getDetails(id);
  }

  /* =========================
     CRÉATION
     ========================= */
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ActualiteResponse create(
    @RequestParam String title,
    @RequestParam String content,
    @RequestParam Integer displayOrder,
    @RequestParam(required = false) Boolean enabled,
    @RequestParam MultipartFile coverImage
  ) {
    return service.create(title, content, displayOrder, enabled, coverImage);
  }

  /* =========================
     MISE À JOUR TEXTE
     ========================= */
  @PutMapping("/{id}")
  public ActualiteResponse update(
    @PathVariable Long id,
    @RequestBody ActualiteUpdateRequest request
  ) {
    return service.update(id, request);
  }

  /* =========================
     MISE À JOUR COUVERTURE
     ========================= */
  @PutMapping(
    value = "/{id}/cover",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ActualiteResponse updateCover(
    @PathVariable Long id,
    @RequestParam MultipartFile coverImage
  ) {
    return service.updateCover(id, coverImage);
  }

  /* =========================
     AJOUT IMAGES GALERIE
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

  /* =========================
     REMPLACEMENT GALERIE
     ========================= */
  @PutMapping(
    value = "/{id}/images",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public void replaceImages(
    @PathVariable Long id,
    @RequestParam("images") List<MultipartFile> images
  ) {
    service.replaceGalleryImages(id, images);
  }

  /* =========================
     ✅ SUPPRESSION IMAGE
     ADMIN + SUPERADMIN
     ========================= */
  @DeleteMapping("/images/{imageId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public void deleteImage(@PathVariable Long imageId) {
    service.deleteGalleryImage(imageId);
  }

  /* =========================
     HISTORIQUE DE PUBLICATION
     ========================= */
  @GetMapping("/{id}/history")
  public List<ActualitePublicationHistoryResponse> history(
    @PathVariable Long id
  ) {
    return service.getPublicationHistory(id);
  }

  /* =========================
     SUPPRESSION ACTUALITÉ
     SUPERADMIN SEULEMENT
     ========================= */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPERADMIN')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  /* =========================
     RÉORDONNANCEMENT
     ========================= */
  @PutMapping("/reorder")
  public void reorder(@RequestBody ActualiteReorderRequest request) {
    service.reorder(request);
  }
}
