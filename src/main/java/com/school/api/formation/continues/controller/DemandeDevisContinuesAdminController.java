package com.school.api.formation.continues.controller;

import com.school.api.formation.continues.dto.RepondreDemandeDevisContinuesDTO;
import com.school.api.formation.continues.service.DemandeDevisContinuesAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/formations-continues/demandes-devis")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class DemandeDevisContinuesAdminController {

  private final DemandeDevisContinuesAdminService service;

  @PostMapping("/{id}/repondre")
  public ResponseEntity<?> repondre(
    @PathVariable Long id,
    @ModelAttribute RepondreDemandeDevisContinuesDTO dto
  ) {
    service.repondre(id, dto);
    return ResponseEntity.ok("Réponse envoyée");
  }

  @PostMapping("/{id}/marquer-traitee")
  public ResponseEntity<?> marquerTraitee(@PathVariable Long id) {
    service.marquerTraitee(id);
    return ResponseEntity.ok("Demande marquée comme traitée");
  }

  @GetMapping
  public ResponseEntity<?> getAll(
    @RequestParam(required = false) String statut,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    return ResponseEntity.ok(service.getDemandes(statut, page, size));
  }
}
