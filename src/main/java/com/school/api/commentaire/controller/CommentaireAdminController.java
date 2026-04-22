package com.school.api.commentaire.controller;

import com.school.api.commentaire.dto.*;
import com.school.api.commentaire.service.CommentaireService;
import com.school.api.auth.audit.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/commentaires")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class CommentaireAdminController {

  private final CommentaireService service;

  @AuditLog(action = "CONSULTATION_COMMENTAIRES")
  @GetMapping
  public List<CommentaireResponse> all() {
    return service.getAll();
  }

  @AuditLog(action = "CREATION_COMMENTAIRE", failureAction = "CREATION_COMMENTAIRE_ECHEC")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public CommentaireResponse create(
          @RequestPart CommentaireCreateRequest request,
          @RequestPart MultipartFile authorImage
  ) {
    return service.create(request, authorImage);
  }

  @AuditLog(action = "MODIFICATION_COMMENTAIRE", target = "#id.toString()", failureAction = "MODIFICATION_COMMENTAIRE_ECHEC")
  @PutMapping("/{id}")
  public CommentaireResponse update(
          @PathVariable Long id,
          @RequestBody CommentaireUpdateRequest request
  ) {
    return service.update(id, request);
  }

  @AuditLog(action = "SUPPRESSION_COMMENTAIRE", target = "#id.toString()", failureAction = "SUPPRESSION_COMMENTAIRE_ECHEC")
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @AuditLog(action = "REORDONNANCEMENT_COMMENTAIRES")
  @PutMapping("/reorder")
  public void reorder(@RequestBody CommentaireReorderRequest request) {
    service.reorder(request);
  }
}