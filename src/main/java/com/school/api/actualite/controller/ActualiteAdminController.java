package com.school.api.actualite.controller;

import com.school.api.actualite.dto.*;
import com.school.api.actualite.service.ActualiteService;
import com.school.api.auth.audit.AuditLog;
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

  @AuditLog(action = "CONSULTATION_ACTUALITES")
  @GetMapping
  public List<ActualiteResponse> all() {
    return service.getAll();
  }

  @AuditLog(action = "CONSULTATION_ACTUALITE", target = "#id.toString()")
  @GetMapping("/{id}")
  public ActualiteDetailsResponse getDetails(@PathVariable Long id) {
    return service.getDetails(id);
  }

  @AuditLog(action = "CREATION_ACTUALITE", target = "#title", failureAction = "CREATION_ACTUALITE_ECHEC")
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

  @AuditLog(action = "MODIFICATION_ACTUALITE", target = "#id.toString()", failureAction = "MODIFICATION_ACTUALITE_ECHEC")
  @PutMapping("/{id}")
  public ActualiteResponse update(
          @PathVariable Long id,
          @RequestBody ActualiteUpdateRequest request
  ) {
    return service.update(id, request);
  }

  @AuditLog(action = "MODIFICATION_COUVERTURE_ACTUALITE", target = "#id.toString()")
  @PutMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ActualiteResponse updateCover(
          @PathVariable Long id,
          @RequestParam MultipartFile coverImage
  ) {
    return service.updateCover(id, coverImage);
  }

  @AuditLog(action = "AJOUT_IMAGES_ACTUALITE", target = "#id.toString()")
  @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void addImages(
          @PathVariable Long id,
          @RequestParam("images") List<MultipartFile> images
  ) {
    service.addGalleryImages(id, images);
  }

  @AuditLog(action = "REMPLACEMENT_IMAGES_ACTUALITE", target = "#id.toString()")
  @PutMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void replaceImages(
          @PathVariable Long id,
          @RequestParam("images") List<MultipartFile> images
  ) {
    service.replaceGalleryImages(id, images);
  }

  @AuditLog(action = "SUPPRESSION_IMAGE_ACTUALITE", target = "#imageId.toString()", failureAction = "SUPPRESSION_IMAGE_ECHEC")
  @DeleteMapping("/images/{imageId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public void deleteImage(@PathVariable Long imageId) {
    service.deleteGalleryImage(imageId);
  }

  @AuditLog(action = "CONSULTATION_HISTORIQUE_ACTUALITE", target = "#id.toString()")
  @GetMapping("/{id}/history")
  public List<ActualitePublicationHistoryResponse> history(@PathVariable Long id) {
    return service.getPublicationHistory(id);
  }

  @AuditLog(action = "SUPPRESSION_ACTUALITE", target = "#id.toString()", failureAction = "SUPPRESSION_ACTUALITE_ECHEC")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPERADMIN')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @AuditLog(action = "REORDONNANCEMENT_ACTUALITES")
  @PutMapping("/reorder")
  public void reorder(@RequestBody ActualiteReorderRequest request) {
    service.reorder(request);
  }
}