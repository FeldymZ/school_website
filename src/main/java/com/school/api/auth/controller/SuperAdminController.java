package com.school.api.auth.controller;

import com.school.api.auth.dto.CreateSecondSuperAdminRequest;
import com.school.api.auth.service.SuperAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SuperAdminController {

  private final SuperAdminService superAdminService;

  /**
   * ⚠️ ENDPOINT UNIQUE
   * Création du DEUXIÈME et DERNIER SUPERADMIN
   *
   * Sécurité :
   * - JWT requis
   * - rôle SUPERADMIN requis
   * - verrou métier en base
   */
  @PostMapping("/superadmin/create")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public ResponseEntity<String> createSecondSuperAdmin(
    @RequestBody @Valid CreateSecondSuperAdminRequest request
  ) {
    superAdminService.createSecondSuperAdmin(request);
    return ResponseEntity.ok("Deuxième SUPERADMIN créé avec succès");
  }
}
