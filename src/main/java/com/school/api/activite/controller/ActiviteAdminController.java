package com.school.api.activite.controller;

import com.school.api.activite.dto.ActiviteRequest;
import com.school.api.activite.dto.ActiviteResponse;
import com.school.api.activite.service.ActiviteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/activites")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class ActiviteAdminController {

  private final ActiviteService activiteService;

    /* =====================================================
       ======================= CREATE ======================
       ===================================================== */

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ActiviteResponse create(
    @Valid @RequestPart ActiviteRequest request,
    @RequestPart MultipartFile photo
  ) {
    if (photo == null || photo.isEmpty()) {
      throw new IllegalArgumentException("La photo est obligatoire");
    }
    return activiteService.create(request, photo);
  }

    /* =====================================================
       ======================= UPDATE ======================
       ===================================================== */

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ActiviteResponse update(
    @PathVariable Long id,
    @Valid @RequestPart ActiviteRequest request,
    @RequestPart(required = false) MultipartFile photo
  ) {
    return activiteService.update(id, request, photo);
  }

    /* =====================================================
       ======================= DELETE ======================
       ===================================================== */

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    activiteService.delete(id);
  }

    /* =====================================================
       ========== DELETE IMAGE (SUPERADMIN ONLY) ============
       ===================================================== */

  @PreAuthorize("hasRole('SUPERADMIN')")
  @DeleteMapping("/images/{imageId}")
  public void deleteImage(@PathVariable Long imageId) {
    activiteService.deleteImage(imageId);
  }

    /* =====================================================
       ======================== READ =======================
       ===================================================== */

  @GetMapping
  public List<ActiviteResponse> getAll() {
    return activiteService.getAll();
  }

  @GetMapping("/{id}")
  public ActiviteResponse getById(@PathVariable Long id) {
    return activiteService.getById(id);
  }
}
