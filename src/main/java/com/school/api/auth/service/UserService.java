package com.school.api.auth.service;

import com.school.api.auth.dto.UserResponse;
import com.school.api.auth.entity.Role;
import com.school.api.auth.entity.User;
import com.school.api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public List<UserResponse> getAll() {
    return userRepository.findAll().stream().map(this::toDto).toList();
  }

  public void disable(Long id, String actorEmail) {
    User user = get(id);
    if (user.getRole() == Role.SUPERADMIN) {
      throw new IllegalStateException("Impossible de désactiver un SUPERADMIN");
    }
    user.setEnabled(false);
    userRepository.save(user);
  }

  public void enable(Long id, String actorEmail) {
    User user = get(id);
    user.setEnabled(true);
    userRepository.save(user);
  }

  public void delete(Long id, String actorEmail) {
    User user = get(id);
    if (user.getRole() == Role.SUPERADMIN) {
      throw new IllegalStateException("Impossible de supprimer un SUPERADMIN");
    }
    userRepository.delete(user);
  }

  public void changeRole(Long id, Role role, String actorEmail) {
    User user = get(id);
    if (user.getRole() == Role.SUPERADMIN || role == Role.SUPERADMIN) {
      throw new IllegalStateException("Action interdite");
    }
    user.setRole(role);
    userRepository.save(user);
  }

  public void changePassword(Long id, String newPassword, String actorEmail) {
    User user = get(id);
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  public void updateMenuAccess(Long id, Set<String> menuAccess, String actorEmail) {
    User user = get(id);
    if (user.getRole() == Role.SUPERADMIN) {
      throw new IllegalStateException("Un SUPERADMIN a déjà accès à tout, inutile de configurer ses menus");
    }
    user.setMenuAccess(AdminService.validateMenuAccess(menuAccess));
    userRepository.save(user);
  }

  public List<UserResponse> filter(Role role, Boolean enabled) {
    if (role != null && enabled != null)
      return userRepository.findByRoleAndEnabled(role, enabled).stream().map(this::toDto).toList();
    if (role != null)
      return userRepository.findByRole(role).stream().map(this::toDto).toList();
    if (enabled != null)
      return userRepository.findByEnabled(enabled).stream().map(this::toDto).toList();
    return getAll();
  }

  public List<UserResponse> searchByEmail(String email) {
    return userRepository.findByEmailContainingIgnoreCase(email).stream().map(this::toDto).toList();
  }

  private User get(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
  }

  private UserResponse toDto(User user) {
    return UserResponse.builder()
            .id(user.getId())
            .nom(user.getNom())           // 🆕
            .prenom(user.getPrenom())     // 🆕
            .email(user.getEmail())
            .photoUrl(user.getPhotoUrl()) // 🆕
            .role(user.getRole().name())
            .enabled(user.getEnabled())
            .menuAccess(user.getMenuAccess())
            .build();
  }
}