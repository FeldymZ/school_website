package com.school.api.formation.continues.controller;

import com.school.api.formation.continues.dto.CreateFormationContinuesDTO;
import com.school.api.formation.continues.entity.FormationContinues;
import com.school.api.formation.continues.service.FormationContinuesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/formations-continues")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class FormationContinuesAdminController {

  private final FormationContinuesService service;

  /* =====================================================
     🟢 CRÉER
     ===================================================== */

  @PostMapping
  public ResponseEntity<FormationContinues> create(
    @ModelAttribute CreateFormationContinuesDTO dto
  ) {
    return ResponseEntity.ok(service.create(dto));
  }

  /* =====================================================
     🟡 MODIFIER
     ===================================================== */

  @PutMapping("/{id}")
  public ResponseEntity<FormationContinues> update(
    @PathVariable Long id,
    @ModelAttribute CreateFormationContinuesDTO dto
  ) {
    return ResponseEntity.ok(service.update(id, dto));
  }

  /* =====================================================
     🔵 LISTE PAGINÉE
     ===================================================== */

  @GetMapping
  public Page<FormationContinues> getAll(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    return service.getAll(page, size);
  }

  /* =====================================================
     🔍 DÉTAIL
     ===================================================== */

  @GetMapping("/{id}")
  public ResponseEntity<FormationContinues> getById(
    @PathVariable Long id
  ) {
    return ResponseEntity.ok(service.getById(id));
  }

  /* =====================================================
     🔁 ACTIVER / DÉSACTIVER
     ===================================================== */

  @PatchMapping("/{id}/toggle")
  public ResponseEntity<String> toggle(@PathVariable Long id) {
    service.toggle(id);
    return ResponseEntity.ok("Statut mis à jour");
  }

  /* =====================================================
     ❌ SUPPRIMER
     ===================================================== */

  @DeleteMapping("/{id}")
  public ResponseEntity<String> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.ok("Formation supprimée");
  }
}
