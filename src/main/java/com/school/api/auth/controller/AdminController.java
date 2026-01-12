package com.school.api.auth.controller;

import com.school.api.auth.dto.CreateAdminRequest;
import com.school.api.auth.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  @PostMapping("/create")
  public ResponseEntity<?> createAdmin(@RequestBody @Valid CreateAdminRequest request) {

    adminService.createAdmin(request);
    return ResponseEntity.ok("Admin créé avec succès");
  }
}
