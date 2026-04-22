package com.school.api.stat.keyfigure.controller;

import com.school.api.stat.keyfigure.dto.KeyFigureOrderRequest;
import com.school.api.stat.keyfigure.dto.KeyFigureRequest;
import com.school.api.stat.keyfigure.dto.KeyFigureResponse;
import com.school.api.stat.keyfigure.service.KeyFigureService;
import com.school.api.auth.audit.AuditLog;
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

  @AuditLog(action = "CONSULTATION_KEY_FIGURES")
  @GetMapping
  public List<KeyFigureResponse> all() {
    return service.getAll();
  }

  @AuditLog(action = "CREATION_KEY_FIGURE", failureAction = "CREATION_KEY_FIGURE_ECHEC")
  @PostMapping
  public KeyFigureResponse create(@RequestBody KeyFigureRequest request) {
    return service.create(request);
  }

  @AuditLog(action = "MODIFICATION_KEY_FIGURE", target = "#id.toString()", failureAction = "MODIFICATION_KEY_FIGURE_ECHEC")
  @PutMapping("/{id}")
  public KeyFigureResponse update(
          @PathVariable Long id,
          @RequestBody KeyFigureRequest request
  ) {
    return service.update(id, request);
  }

  @AuditLog(action = "SUPPRESSION_KEY_FIGURE", target = "#id.toString()", failureAction = "SUPPRESSION_KEY_FIGURE_ECHEC")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @AuditLog(action = "REORDONNANCEMENT_KEY_FIGURES")
  @PutMapping("/reorder")
  public void reorder(@RequestBody List<KeyFigureOrderRequest> orders) {
    service.reorder(orders);
  }
}