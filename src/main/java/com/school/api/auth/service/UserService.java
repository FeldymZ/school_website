package com.school.api.auth.service;

import com.school.api.auth.dto.UserResponse;
import com.school.api.auth.entity.Role;
import com.school.api.auth.entity.User;
import com.school.api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  // ✅ Plus besoin d'AdminAuditService ici : l'AOP s'en charge via @AuditLog

  /* ===================== LISTE ===================== */

  public List<UserResponse> getAll() {
    return userRepository.findAll().stream().map(this::toDto).toList();
  }

  /* ===================== DESACTIVER ===================== */

  public void disable(Long id, String actorEmail) {
    User user = get(id);
    if (user.getRole() == Role.SUPERADMIN) {
      throw new IllegalStateException("Impossible de désactiver un SUPERADMIN");
    }
    user.setEnabled(false);
    userRepository.save(user);
  }

  /* ===================== ACTIVER ===================== */

  public void enable(Long id, String actorEmail) {
    User user = get(id);
    user.setEnabled(true);
    userRepository.save(user);
  }

  /* ===================== SUPPRIMER ===================== */

  public void delete(Long id, String actorEmail) {
    User user = get(id);
    if (user.getRole() == Role.SUPERADMIN) {
      throw new IllegalStateException("Impossible de supprimer un SUPERADMIN");
    }
    userRepository.delete(user);
  }

  /* ===================== CHANGER ROLE ===================== */

  public void changeRole(Long id, Role role, String actorEmail) {
    User user = get(id);
    if (user.getRole() == Role.SUPERADMIN || role == Role.SUPERADMIN) {
      throw new IllegalStateException("Action interdite");
    }
    user.setRole(role);
    userRepository.save(user);
  }

  /* ===================== CHANGER MOT DE PASSE ===================== */

  public void changePassword(Long id, String newPassword, String actorEmail) {
    User user = get(id);
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  /* ===================== FILTRES ===================== */

  public List<UserResponse> filter(Role role, Boolean enabled) {
    if (role != null && enabled != null)
      return userRepository.findByRoleAndEnabled(role, enabled).stream().map(this::toDto).toList();
    if (role != null)
      return userRepository.findByRole(role).stream().map(this::toDto).toList();
    if (enabled != null)
      return userRepository.findByEnabled(enabled).stream().map(this::toDto).toList();
    return getAll();
  }

  /* ===================== RECHERCHE ===================== */

  public List<UserResponse> searchByEmail(String email) {
    return userRepository.findByEmailContainingIgnoreCase(email).stream().map(this::toDto).toList();
  }

  /* ===================== UTILS ===================== */

  private User get(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
  }

  private UserResponse toDto(User user) {
    return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .role(user.getRole().name())
            .enabled(user.getEnabled())
            .build();
  }
}