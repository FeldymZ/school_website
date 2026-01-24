package com.school.api.formation.initiale.controller;

import com.school.api.formation.initiale.dto.FormationInitialeDetailsResponse;
import com.school.api.formation.initiale.dto.FormationInitialeResponse;
import com.school.api.formation.initiale.entity.FormationInitialeLevel;
import com.school.api.formation.initiale.service.FormationInitialeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/formations/initiale")
@RequiredArgsConstructor
public class FormationInitialePublicController {

  private final FormationInitialeService service;

  /* ============================
     📋 TOUTES LES FORMATIONS
     ============================ */
  @GetMapping
  public List<FormationInitialeResponse> getAll() {
    return service.getAllPublic();
  }

  /* ============================
     📋 LISTE PAR NIVEAU
     ============================ */
  @GetMapping("/level/{level}")
  public List<FormationInitialeResponse> getByLevel(
    @PathVariable FormationInitialeLevel level
  ) {
    return service.getPublic(level);
  }

  /* ============================
     🔎 DÉTAILS
     ============================ */
  @GetMapping("/{id}")
  public FormationInitialeDetailsResponse details(
    @PathVariable Long id
  ) {
    return service.getDetails(id);
  }
}
