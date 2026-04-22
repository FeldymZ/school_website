package com.school.api.auth.controller;

import com.school.api.auth.audit.AuditLog;
import com.school.api.auth.dto.CreateAdminRequest;
import com.school.api.auth.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  @AuditLog(action = "CREATION_ADMIN", target = "#request.email", failureAction = "CREATION_ADMIN_ECHEC")
  @PostMapping("/create")
  public ResponseEntity<?> createAdmin(
    @RequestBody @Valid CreateAdminRequest request,
    Authentication auth
  ) {
    adminService.createAdmin(request);
    return ResponseEntity.ok("Admin créé avec succès");
  }
}
