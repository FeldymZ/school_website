package com.school.api.activite.controller;

import com.school.api.activite.dto.ActiviteResponse;
import com.school.api.activite.service.ActiviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/activites")
@RequiredArgsConstructor
public class ActivitePublicController {

  private final ActiviteService activiteService;

    /* =====================================================
       ===================== READ ONLY =====================
       ===================================================== */

  @GetMapping
  public List<ActiviteResponse> getAll() {
    return activiteService.getAll();
  }

  @GetMapping("/{id}")
  public ActiviteResponse getById(@PathVariable Long id) {
    return activiteService.getById(id);
  }
}
