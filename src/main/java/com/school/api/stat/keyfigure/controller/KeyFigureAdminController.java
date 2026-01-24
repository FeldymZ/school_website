package com.school.api.stat.keyfigure.controller;

import com.school.api.stat.keyfigure.dto.KeyFigureOrderRequest;
import com.school.api.stat.keyfigure.dto.KeyFigureRequest;
import com.school.api.stat.keyfigure.dto.KeyFigureResponse;
import com.school.api.stat.keyfigure.service.KeyFigureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/admin/key-figures")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class KeyFigureAdminController {

  private final KeyFigureService service;

  /* ============================
     📋 LISTE
     ============================ */

  @GetMapping
  public List<KeyFigureResponse> all() {
    return service.getAll();
  }

  /* ============================
     ➕ CRÉATION
     ============================ */

  @PostMapping
  public KeyFigureResponse create(@RequestBody KeyFigureRequest request) {
    return service.create(request);
  }

  /* ============================
     ✏️ MODIFICATION
     ============================ */

  @PutMapping("/{id}")
  public KeyFigureResponse update(
    @PathVariable Long id,
    @RequestBody KeyFigureRequest request
  ) {
    return service.update(id, request);
  }

  /* ============================
     🗑️ SUPPRESSION
     ============================ */

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  /* ============================
   🔀 CHANGER L’ORDRE
   ============================ */

  @PutMapping("/reorder")
  public void reorder(@RequestBody List<KeyFigureOrderRequest> orders) {
    service.reorder(orders);
  }


}
