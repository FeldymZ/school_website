package com.school.api.activite.controller;

import com.school.api.activite.dto.ActiviteResponse;
import com.school.api.activite.service.ActiviteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/activites")
public class ActivitePublicController {

  private final ActiviteService activiteService;

  public ActivitePublicController(ActiviteService activiteService) {
    this.activiteService = activiteService;
  }

    /* =====================================================
       ===================== LIST ==========================
       ===================================================== */
  /**
   * Liste publique des activités
   * - accessible sans authentification
   * - retourne activités + médias (photos / vidéo)
   */
  @GetMapping
  public List<ActiviteResponse> getAll() {
    return activiteService.getAll();
  }

    /* =====================================================
       ===================== DETAIL ========================
       ===================================================== */
  /**
   * Détail d'une activité
   * - accessible sans authentification
   */
  @GetMapping("/{id}")
  public ActiviteResponse getById(@PathVariable Long id) {
    return activiteService.getById(id);
  }
}
