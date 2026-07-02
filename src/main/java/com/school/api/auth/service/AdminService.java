package com.school.api.auth.service;

import com.school.api.auth.dto.CreateAdminRequest;
import com.school.api.auth.entity.MenuPermission;
import com.school.api.auth.entity.Role;
import com.school.api.auth.entity.User;
import com.school.api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public void createAdmin(CreateAdminRequest request) {

    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new IllegalStateException("Email déjà utilisé");
    }

    Set<String> menuAccess = validateMenuAccess(request.menuAccess());

    User admin = User.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .role(Role.ADMIN)
            .enabled(true)
            .menuAccess(menuAccess)
            .build();

    userRepository.save(admin);
  }

  public void changeRole(Long userId, Role newRole) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

    if (user.getRole() == Role.SUPERADMIN) {
      throw new IllegalStateException("Impossible de modifier le rôle d'un SUPERADMIN");
    }
    if (newRole == Role.SUPERADMIN) {
      throw new IllegalStateException("Impossible d'attribuer le rôle SUPERADMIN");
    }

    user.setRole(newRole);
    userRepository.save(user);
  }

  // 🆕 réutilisée aussi par UserService.updateMenuAccess
  static Set<String> validateMenuAccess(Set<String> requested) {
    if (requested == null) return Set.of();
    for (String key : requested) {
      if (!MenuPermission.isValid(key)) {
        throw new IllegalArgumentException("Clé de permission invalide : " + key);
      }
    }
    return requested;
  }
}