package com.school.api.activite.controller;

import com.school.api.activite.dto.ActiviteRequest;
import com.school.api.activite.dto.ActiviteResponse;
import com.school.api.activite.service.ActiviteService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/activites")
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class ActiviteAdminController {

  private final ActiviteService activiteService;

  public ActiviteAdminController(ActiviteService activiteService) {
    this.activiteService = activiteService;
  }

    /* =====================================================
       ======================= CREATE ======================
       ===================================================== */
  /**
   * Création d'une activité
   * - form-data
   * - au moins 1 photo obligatoire
   * - plusieurs photos possibles
   * - 1 vidéo optionnelle
   */
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ActiviteResponse create(
    @RequestParam @NotBlank String titre,
    @RequestParam @NotBlank String contenu,
    @RequestParam("photos") MultipartFile[] photos,
    @RequestParam(value = "video", required = false) MultipartFile video
  ) {

    if (photos == null || photos.length == 0) {
      throw new IllegalArgumentException("Au moins une photo est obligatoire");
    }

    ActiviteRequest request = new ActiviteRequest();
    request.setTitre(titre);
    request.setContenu(contenu);

    return activiteService.create(request, photos, video);
  }

    /* =====================================================
       ======================= READ ========================
       ===================================================== */

  @GetMapping
  public List<ActiviteResponse> getAll() {
    return activiteService.getAll();
  }

  @GetMapping("/{id}")
  public ActiviteResponse getById(@PathVariable Long id) {
    return activiteService.getById(id);
  }

    /* =====================================================
       ======================= DELETE ======================
       ===================================================== */

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    activiteService.delete(id);
  }

    /* =====================================================
       ============ DELETE MEDIA (SUPERADMIN) ==============
       ===================================================== */
  /**
   * Suppression d'une image ou vidéo
   * - suppression DB + fichier
   * - SUPERADMIN uniquement
   */
  @PreAuthorize("hasRole('SUPERADMIN')")
  @DeleteMapping("/medias/{mediaId}")
  public void deleteMedia(@PathVariable Long mediaId) {
    activiteService.deleteMedia(mediaId);
  }
}
