package com.school.api.auth.controller;

import com.school.api.auth.audit.AuditLog;
import com.school.api.auth.dto.ChangePasswordRequest;
import com.school.api.auth.dto.ChangeRoleRequest;
import com.school.api.auth.dto.UpdateMenuAccessRequest;
import com.school.api.auth.dto.UserResponse;
import com.school.api.auth.entity.Role;
import com.school.api.auth.security.RequiresMenuAccess;
import com.school.api.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

  private final UserService userService;

  /* ===================== LISTE ===================== */

  @AuditLog(action = "CONSULTATION_UTILISATEURS")
  @RequiresMenuAccess("ADMINISTRATION_UTILISATEURS")
  @GetMapping
  public List<UserResponse> all() {
    return userService.getAll();
  }

  /* ===================== DESACTIVER ===================== */

  @AuditLog(action = "DESACTIVATION_UTILISATEUR", target = "#id.toString()", failureAction = "DESACTIVATION_ECHEC")
  @RequiresMenuAccess("ADMINISTRATION_UTILISATEURS")
  @PatchMapping("/{id}/desactiver")
  public ResponseEntity<Void> disable(
          @PathVariable Long id,
          Authentication auth
  ) {
    userService.disable(id, auth.getName());
    return ResponseEntity.noContent().build();
  }

  /* ===================== ACTIVER ===================== */

  @AuditLog(action = "ACTIVATION_UTILISATEUR", target = "#id.toString()", failureAction = "ACTIVATION_ECHEC")
  @RequiresMenuAccess("ADMINISTRATION_UTILISATEURS")
  @PatchMapping("/{id}/activer")
  public ResponseEntity<Void> enable(
          @PathVariable Long id,
          Authentication auth
  ) {
    userService.enable(id, auth.getName());
    return ResponseEntity.noContent().build();
  }

  /* ===================== SUPPRIMER ===================== */

  @AuditLog(action = "SUPPRESSION_UTILISATEUR", target = "#id.toString()", failureAction = "SUPPRESSION_ECHEC")
  @RequiresMenuAccess("ADMINISTRATION_UTILISATEURS")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
          @PathVariable Long id,
          Authentication auth
  ) {
    userService.delete(id, auth.getName());
    return ResponseEntity.noContent().build();
  }

  /* ===================== CHANGER ROLE ===================== */

  @AuditLog(action = "CHANGEMENT_ROLE", target = "#id.toString()", failureAction = "CHANGEMENT_ROLE_ECHEC")
  @RequiresMenuAccess("ADMINISTRATION_UTILISATEURS")
  @PatchMapping("/{id}/role")
  public ResponseEntity<Void> changeRole(
          @PathVariable Long id,
          @RequestBody ChangeRoleRequest request,
          Authentication auth
  ) {
    userService.changeRole(id, request.role(), auth.getName());
    return ResponseEntity.noContent().build();
  }

  /* ===================== CHANGER MOT DE PASSE ===================== */

  @AuditLog(action = "CHANGEMENT_MOT_DE_PASSE", target = "#id.toString()", failureAction = "CHANGEMENT_MOT_DE_PASSE_ECHEC")
  @RequiresMenuAccess("ADMINISTRATION_UTILISATEURS")
  @PatchMapping("/{id}/password")
  public ResponseEntity<Void> changePassword(
          @PathVariable Long id,
          @RequestBody ChangePasswordRequest request,
          Authentication auth
  ) {
    userService.changePassword(id, request.password(), auth.getName());
    return ResponseEntity.noContent().build();
  }

  /* ===================== FILTRES ===================== */

  @AuditLog(action = "FILTRE_UTILISATEURS")
  @RequiresMenuAccess("ADMINISTRATION_UTILISATEURS")
  @GetMapping("/filter")
  public List<UserResponse> filter(
          @RequestParam(required = false) Role role,
          @RequestParam(required = false) Boolean enabled
  ) {
    return userService.filter(role, enabled);
  }

  /* ===================== MENU ACCESS — SUPERADMIN UNIQUEMENT (🔒) ===================== */

  @AuditLog(action = "CHANGEMENT_MENU_ACCESS", target = "#id.toString()", failureAction = "CHANGEMENT_MENU_ACCESS_ECHEC")
  @PreAuthorize("hasRole('SUPERADMIN')")
  @PatchMapping("/{id}/menu-access")
  public ResponseEntity<Void> updateMenuAccess(
          @PathVariable Long id,
          @RequestBody @Valid UpdateMenuAccessRequest request,
          Authentication auth
  ) {
    userService.updateMenuAccess(id, request.menuAccess(), auth.getName());
    return ResponseEntity.noContent().build();
  }

  /* ===================== RECHERCHE ===================== */

  @AuditLog(action = "RECHERCHE_UTILISATEUR", target = "#email")
  @RequiresMenuAccess("ADMINISTRATION_UTILISATEURS")
  @GetMapping("/search")
  public List<UserResponse> search(@RequestParam String email) {
    return userService.searchByEmail(email);
  }
}