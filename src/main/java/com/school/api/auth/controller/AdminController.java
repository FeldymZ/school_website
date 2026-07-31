package com.school.api.auth.controller;

import com.school.api.auth.audit.AuditLog;
import com.school.api.auth.dto.CreateAdminRequest;
import com.school.api.auth.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  @AuditLog(
          action = "CREATION_ADMIN",
          target = "#request.email",
          failureAction = "CREATION_ADMIN_ECHEC"
  )
  @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> createAdmin(
          @RequestPart("data") @Valid CreateAdminRequest request,
          @RequestPart(value = "photo", required = false) MultipartFile photo,
          Authentication auth
  ) {
    adminService.createAdmin(request, photo);
    return ResponseEntity.ok("Admin créé avec succès");
  }
}