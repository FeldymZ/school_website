package com.school.api.formation.continues.controller;

import com.school.api.formation.continues.entity.FormationContinues;
import com.school.api.formation.continues.service.FormationContinuesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/formations-continues")
@RequiredArgsConstructor
public class FormationContinuesPublicController {

  private final FormationContinuesService service;

  /* =====================================================
     📄 LISTE PUBLIQUE (enabled = true)
     ===================================================== */

  @GetMapping
  public Page<FormationContinues> getAllPublic(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    return service.getAllPublic(page, size);
  }

  /* =====================================================
     🔍 DÉTAIL PAR SLUG
     ===================================================== */

  @GetMapping("/slug/{slug}")
  public FormationContinues getBySlug(
    @PathVariable String slug
  ) {
    return service.getBySlug(slug);
  }
}
