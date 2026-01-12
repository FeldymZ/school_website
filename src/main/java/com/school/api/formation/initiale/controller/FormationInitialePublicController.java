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
     📋 LISTE (LICENCE / MASTER)
     ============================ */

  @GetMapping("/{level}")
  public List<FormationInitialeResponse> getByLevel(
    @PathVariable FormationInitialeLevel level
  ) {
    return service.getPublic(level);
  }

  /* ============================
     🔎 DÉTAILS
     ============================ */

  @GetMapping("/{id}/details")
  public FormationInitialeDetailsResponse details(@PathVariable Long id) {
    return service.getDetails(id);
  }
}
