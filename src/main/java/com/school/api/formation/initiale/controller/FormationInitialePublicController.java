package com.school.api.formation.initiale.controller;

import com.school.api.formation.initiale.dto.FormationInitialeDetailsResponse;
import com.school.api.formation.initiale.dto.FormationInitialeResponse;
import com.school.api.formation.initiale.entity.FormationInitialeLevel;
import com.school.api.formation.initiale.service.FormationInitialeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/public/formations/initiale")
@RequiredArgsConstructor
public class FormationInitialePublicController {

  private final FormationInitialeService service;

  /* ============================
     📋 TOUTES LES FORMATIONS (PUBLIC)
     ============================ */
  @GetMapping
  public List<FormationInitialeResponse> getAll() {
    return service.getAllPublic();
  }

  /* ============================
     📋 LISTE PAR NIVEAU (PUBLIC)
     ============================ */
  @GetMapping("/level/{level}")
  public List<FormationInitialeResponse> getByLevel(
    @PathVariable FormationInitialeLevel level
  ) {
    return service.getPublic(level);
  }

  /* ============================
     ❌ ACCÈS PAR ID INTERDIT (PUBLIC)
     ============================ */
  @GetMapping("/{id}")
  public void forbiddenById() {
    throw new ResponseStatusException(
      HttpStatus.NOT_FOUND,
      "Cette ressource n'existe plus. Utilisez le slug."
    );
  }

  /* ============================
     ✅ DÉTAILS PAR SLUG (URL CANONIQUE)
     ============================ */
  @GetMapping("/slug/{slug}")
  public FormationInitialeDetailsResponse detailsBySlug(
    @PathVariable String slug
  ) {
    return service.getPublicDetailsBySlug(slug);
  }
}
