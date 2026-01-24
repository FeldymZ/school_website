package com.school.api.formation.preinscription.controller;

import com.school.api.formation.preinscription.dto.PreinscriptionDecisionRequest;
import com.school.api.formation.preinscription.entity.FormationPreinscription;
import com.school.api.formation.preinscription.entity.enums.StatutPreinscription;
import com.school.api.formation.preinscription.service.FormationPreinscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/admin/preinscriptions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class FormationPreinscriptionAdminController {

  private final FormationPreinscriptionService service;

  /* =====================================================
     LISTE DES PRÉINSCRIPTIONS
     ===================================================== */

  @GetMapping
  public List<FormationPreinscription> getAll() {
    return service.getAll();
  }

  @GetMapping("/statut/{statut}")
  public List<FormationPreinscription> getByStatut(
    @PathVariable StatutPreinscription statut
  ) {
    return service.getByStatut(statut);
  }

  /* =====================================================
     DÉCISION : VALIDER / REJETER
     ===================================================== */

  @PostMapping("/{id}/decision")
  public ResponseEntity<Void> decide(
    @PathVariable Long id,
    @RequestBody PreinscriptionDecisionRequest request
  ) {
    service.decide(id, request.accepted(), request.commentaire());
    return ResponseEntity.ok().build();
  }

  /* =====================================================
     TÉLÉCHARGEMENT WORD
     ===================================================== */

  @GetMapping("/{id}/download")
  public ResponseEntity<byte[]> download(@PathVariable Long id) throws Exception {

    File file = service.downloadWord(id);

    byte[] content = Files.readAllBytes(file.toPath());

    return ResponseEntity.ok()
      .header(
        "Content-Disposition",
        "attachment; filename=preinscription-" + id + ".docx"
      )
      .body(content);
  }
}
