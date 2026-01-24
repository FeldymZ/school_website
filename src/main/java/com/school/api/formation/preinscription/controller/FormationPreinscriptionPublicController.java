package com.school.api.formation.preinscription.controller;

import com.school.api.formation.preinscription.dto.FormationPreinscriptionRequest;
import com.school.api.formation.preinscription.service.FormationPreinscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/preinscriptions")
@RequiredArgsConstructor
public class FormationPreinscriptionPublicController {

  private final FormationPreinscriptionService service;

  @PostMapping
  public ResponseEntity<Void> preinscrire(
    @RequestBody @Valid FormationPreinscriptionRequest request
  ) {
    service.preinscrire(request);
    return ResponseEntity.ok().build();
  }
}
