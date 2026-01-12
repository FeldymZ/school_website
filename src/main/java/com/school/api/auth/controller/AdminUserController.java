package com.school.api.auth.controller;

import com.school.api.auth.dto.ChangeRoleRequest;
import com.school.api.auth.dto.UserResponse;
import com.school.api.auth.entity.Role;
import com.school.api.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

  private final UserService userService;

  // 📋 Liste des utilisateurs
  @GetMapping
  public List<UserResponse> all() {
    return userService.getAll();
  }

  // 🔴 Désactiver
  @PatchMapping("/{id}/desactiver")
  public ResponseEntity<Void> disable(
    @PathVariable Long id,
    Authentication auth
  ) {
    userService.disable(id, auth.getName());
    return ResponseEntity.noContent().build();
  }

  // 🟢 Activer
  @PatchMapping("/{id}/activer")
  public ResponseEntity<Void> enable(
    @PathVariable Long id,
    Authentication auth
  ) {
    userService.enable(id, auth.getName());
    return ResponseEntity.noContent().build();
  }

  // 🔁 Changer le rôle
  @PatchMapping("/{id}/role")
  public ResponseEntity<Void> changeRole(
    @PathVariable Long id,
    @RequestBody ChangeRoleRequest request,
    Authentication auth
  ) {
    userService.changeRole(id, request.role(), auth.getName());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/filter")
  public List<UserResponse> filter(
    @RequestParam(required = false) Role role,
    @RequestParam(required = false) Boolean enabled
  ) {
    return userService.filter(role, enabled);
  }

  @GetMapping("/search")
  public List<UserResponse> search(@RequestParam String email) {
    return userService.searchByEmail(email);
  }


}
