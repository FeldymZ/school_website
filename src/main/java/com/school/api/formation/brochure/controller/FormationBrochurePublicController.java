package com.school.api.formation.brochure.controller;

import com.school.api.formation.brochure.dto.FormationBrochureRequestDto;
import com.school.api.formation.brochure.service.FormationBrochureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/formations/initiale")
@RequiredArgsConstructor
public class FormationBrochurePublicController {

  private final FormationBrochureService service;

  /* ============================
     📩 DEMANDE DE BROCHURE (SLUG)
     ============================ */
  @PostMapping("/slug/{slug}/brochure")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void requestBrochure(
    @PathVariable String slug,
    @RequestBody @Valid FormationBrochureRequestDto request
  ) {
    service.sendBrochureBySlug(slug, request);
  }

  /* ============================
     ❌ BLOCAGE ID EN PUBLIC
     ============================ */
  @PostMapping("/{formationId}/brochure")
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public void forbiddenById() {
    // accès public par ID interdit
  }
}
