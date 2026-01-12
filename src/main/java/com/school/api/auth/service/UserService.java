package com.school.api.auth.service;

import com.school.api.auth.dto.UserResponse;
import com.school.api.auth.entity.Role;
import com.school.api.auth.entity.User;
import com.school.api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final AdminAuditService auditService;

  // 📋 Liste
  public List<UserResponse> getAll() {
    return userRepository.findAll().stream()
      .map(this::toDto)
      .toList();
  }

  // 🔴 Désactiver
  public void disable(Long id, String actorEmail) {

    User user = get(id);

    if (user.getRole() == Role.SUPERADMIN) {
      throw new IllegalStateException("Impossible de désactiver un SUPERADMIN");
    }

    user.setEnabled(false);
    userRepository.save(user);

    auditService.log(
      actorEmail,
      "DESACTIVATION_UTILISATEUR",
      user.getEmail()
    );
  }

  // 🟢 Activer
  public void enable(Long id, String actorEmail) {

    User user = get(id);

    user.setEnabled(true);
    userRepository.save(user);

    auditService.log(
      actorEmail,
      "ACTIVATION_UTILISATEUR",
      user.getEmail()
    );
  }

  // 🔁 Changer rôle
  public void changeRole(Long id, Role role, String actorEmail) {

    User user = get(id);

    if (user.getRole() == Role.SUPERADMIN || role == Role.SUPERADMIN) {
      throw new IllegalStateException("Action interdite");
    }

    user.setRole(role);
    userRepository.save(user);

    auditService.log(
      actorEmail,
      "CHANGEMENT_ROLE -> " + role.name(),
      user.getEmail()
    );
  }

  // 🔍 Utils
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

  public List<UserResponse> filter(Role role, Boolean enabled) {

    if (role != null && enabled != null) {
      return userRepository.findByRoleAndEnabled(role, enabled)
        .stream().map(this::toDto).toList();
    }

    if (role != null) {
      return userRepository.findByRole(role)
        .stream().map(this::toDto).toList();
    }

    if (enabled != null) {
      return userRepository.findByEnabled(enabled)
        .stream().map(this::toDto).toList();
    }

    return getAll();
  }

  // 🔍 Recherche par email (LIKE)
  public List<UserResponse> searchByEmail(String email) {
    return userRepository
      .findByEmailContainingIgnoreCase(email)
      .stream()
      .map(this::toDto)
      .toList();
  }



}
