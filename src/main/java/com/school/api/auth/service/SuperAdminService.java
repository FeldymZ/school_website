package com.school.api.auth.service;

import com.school.api.auth.dto.CreateSecondSuperAdminRequest;
import com.school.api.auth.entity.Role;
import com.school.api.auth.entity.User;
import com.school.api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * Création UNIQUE du second SUPERADMIN
   * - Le premier est créé au bootstrap
   * - Maximum autorisé : 2 SUPERADMIN
   */
  public void createSecondSuperAdmin(CreateSecondSuperAdminRequest request) {

    long superAdminCount = userRepository.countByRole(Role.SUPERADMIN);

    // 🔒 VERROU MÉTIER ABSOLU
    if (superAdminCount >= 2) {
      throw new IllegalStateException(
        "Impossible de créer un autre SUPERADMIN : la limite de 2 est atteinte"
      );
    }

    // 🔒 Sécurité supplémentaire (email unique)
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new IllegalStateException("Cet email est déjà utilisé");
    }

    User superAdmin = User.builder()
      .email(request.email())
      .password(passwordEncoder.encode(request.password()))
      .role(Role.SUPERADMIN)
      .enabled(true)
      .build();

    userRepository.save(superAdmin);
  }
}
