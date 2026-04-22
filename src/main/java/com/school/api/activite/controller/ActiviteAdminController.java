package com.school.api.activite.controller;

import com.school.api.activite.dto.ActiviteRequest;
import com.school.api.activite.dto.ActiviteResponse;
import com.school.api.activite.service.ActiviteService;
import com.school.api.auth.audit.AuditLog;
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

  @AuditLog(action = "CREATION_ACTIVITE", target = "#titre", failureAction = "CREATION_ACTIVITE_ECHEC")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ActiviteResponse create(
          @RequestParam @NotBlank String titre,
          @RequestParam @NotBlank String contenu,
          @RequestParam("photos") MultipartFile[] photos,
          @RequestParam(value = "video", required = false) MultipartFile video
  ) {
    ActiviteRequest request = new ActiviteRequest();
    request.setTitre(titre);
    request.setContenu(contenu);
    return activiteService.create(request, photos, video);
  }

  @AuditLog(action = "MODIFICATION_ACTIVITE", target = "#id.toString()", failureAction = "MODIFICATION_ACTIVITE_ECHEC")
  @PutMapping("/{id}")
  public ActiviteResponse update(
          @PathVariable Long id,
          @RequestBody ActiviteRequest request
  ) {
    return activiteService.update(id, request);
  }

  @AuditLog(action = "AJOUT_MEDIAS_ACTIVITE", target = "#id.toString()")
  @PostMapping(value = "/{id}/medias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ActiviteResponse addMedias(
          @PathVariable Long id,
          @RequestParam(value = "photos", required = false) MultipartFile[] photos,
          @RequestParam(value = "video", required = false) MultipartFile video
  ) {
    return activiteService.addMedias(id, photos, video);
  }

  @AuditLog(action = "CONSULTATION_ACTIVITES")
  @GetMapping
  public List<ActiviteResponse> getAll() {
    return activiteService.getAll();
  }

  @AuditLog(action = "CONSULTATION_ACTIVITE", target = "#id.toString()")
  @GetMapping("/{id}")
  public ActiviteResponse getById(@PathVariable Long id) {
    return activiteService.getById(id);
  }

  @AuditLog(action = "SUPPRESSION_ACTIVITE", target = "#id.toString()", failureAction = "SUPPRESSION_ACTIVITE_ECHEC")
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    activiteService.delete(id);
  }

  @AuditLog(action = "SUPPRESSION_MEDIA_ACTIVITE", target = "#mediaId.toString()", failureAction = "SUPPRESSION_MEDIA_ECHEC")
  @PreAuthorize("hasRole('SUPERADMIN')")
  @DeleteMapping("/medias/{mediaId}")
  public void deleteMedia(@PathVariable Long mediaId) {
    activiteService.deleteMedia(mediaId);
  }
}