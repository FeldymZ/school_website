package com.school.api.formation.continues.controller;

import com.school.api.formation.continues.dto.CreateDemandeDevisContinuesDTO;
import com.school.api.formation.continues.service.DemandeDevisContinuesPublicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/formations-continues")
@RequiredArgsConstructor
public class DemandeDevisContinuesPublicController {

  private final DemandeDevisContinuesPublicService service;

  @PostMapping("/{id}/demande-devis")
  public ResponseEntity<?> create(
    @PathVariable Long id,
    @RequestBody @Valid CreateDemandeDevisContinuesDTO dto
  ) {
    service.create(id, dto);
    return ResponseEntity.ok("Demande envoyée");
  }
}
