package com.school.api.auth.controller;

import com.school.api.auth.dto.CreateSecondSuperAdminRequest;
import com.school.api.auth.service.SuperAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SuperAdminController {

  private final SuperAdminService superAdminService;

  @PostMapping("/superadmin/create")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public ResponseEntity<String> createSecondSuperAdmin(
          @RequestBody @Valid CreateSecondSuperAdminRequest request,
          Authentication auth                                         // ✅ FIX : acteur récupéré
  ) {
    superAdminService.createSecondSuperAdmin(request, auth.getName()); // ✅ FIX : email transmis
    return ResponseEntity.ok("Deuxième SUPERADMIN créé avec succès");
  }
}