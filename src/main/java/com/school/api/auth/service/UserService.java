package com.school.api.auth.service;

import com.school.api.auth.dto.PhotoResponse;
import com.school.api.auth.dto.UpdateUserInfoRequest;
import com.school.api.auth.dto.UserResponse;
import com.school.api.auth.entity.Role;
import com.school.api.auth.entity.User;
import com.school.api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  private static final long MAX_PHOTO_SIZE = 3L * 1024 * 1024; // 3 Mo

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

  // Lecture de la photo, pour l'endpoint GET /api/admin/users/{id}/photo (gestion d'autres utilisateurs)
  public PhotoResponse getPhoto(Long id) {
    User user = get(id);
    return buildPhotoResponse(user);
  }

  // Photo de l'utilisateur connecté — pour GET /api/me/photo
  public PhotoResponse getMyPhoto(String email) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    return buildPhotoResponse(user);
  }

  // 🆕 Modifier un autre utilisateur (nom, prénom, email, photo) — gestion admin
  public UserResponse updateInfo(Long id, UpdateUserInfoRequest request, MultipartFile photo, String actorEmail) {
    User user = get(id);

    if (user.getRole() == Role.SUPERADMIN) {
      throw new IllegalStateException("Impossible de modifier un SUPERADMIN via cette route");
    }

    applyInfoUpdate(user, request, photo);
    userRepository.save(user);
    return toDto(user);
  }

  // 🆕 Modifier son propre profil (nom, prénom, email, photo)
  public UserResponse updateMyInfo(String email, UpdateUserInfoRequest request, MultipartFile photo) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

    applyInfoUpdate(user, request, photo);
    userRepository.save(user);
    return toDto(user);
  }

  private void applyInfoUpdate(User user, UpdateUserInfoRequest request, MultipartFile photo) {

    // Vérifie l'unicité de l'email (en excluant l'utilisateur lui-même)
    userRepository.findByEmail(request.email()).ifPresent(existing -> {
      if (!existing.getId().equals(user.getId())) {
        throw new IllegalStateException("Cet email est déjà utilisé par un autre compte");
      }
    });

    user.setNom(request.nom());
    user.setPrenom(request.prenom());
    user.setEmail(request.email());

    if (request.removePhoto()) {
      user.setPhoto(null);
      user.setPhotoContentType(null);
    }

    if (photo != null && !photo.isEmpty()) {

      if (photo.getSize() > MAX_PHOTO_SIZE) {
        throw new IllegalArgumentException("La photo ne doit pas dépasser 3 Mo");
      }

      String contentType = photo.getContentType();
      if (contentType == null || !contentType.startsWith("image/")) {
        throw new IllegalArgumentException("Le fichier envoyé doit être une image");
      }

      try {
        user.setPhoto(photo.getBytes());
        user.setPhotoContentType(contentType);
      } catch (IOException e) {
        throw new RuntimeException("Erreur lors de la lecture de la photo", e);
      }
    }
  }

  private PhotoResponse buildPhotoResponse(User user) {
    if (user.getPhoto() == null || user.getPhoto().length == 0) {
      throw new RuntimeException("Aucune photo pour cet utilisateur");
    }
    String contentType = user.getPhotoContentType() != null
            ? user.getPhotoContentType()
            : "image/jpeg";
    return new PhotoResponse(user.getPhoto(), contentType);
  }

  private User get(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
  }

  private UserResponse toDto(User user) {
    return UserResponse.builder()
            .id(user.getId())
            .nom(user.getNom())
            .prenom(user.getPrenom())
            .email(user.getEmail())
            .hasPhoto(user.getPhoto() != null && user.getPhoto().length > 0)
            .role(user.getRole().name())
            .enabled(user.getEnabled())
            .menuAccess(user.getMenuAccess())
            .build();
  }
}