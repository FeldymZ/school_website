package com.school.api.auth.service;

import com.school.api.auth.dto.CreateAdminRequest;
import com.school.api.auth.entity.MenuPermission;
import com.school.api.auth.entity.Role;
import com.school.api.auth.entity.User;
import com.school.api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final long MAX_PHOTO_SIZE = 3L * 1024 * 1024; // 3 Mo

    public void createAdmin(CreateAdminRequest request, MultipartFile photo) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalStateException("Email déjà utilisé");
        }

        Set<String> menuAccess = validateMenuAccess(request.menuAccess());

        byte[] photoBytes = null;
        String photoContentType = null;

        if (photo != null && !photo.isEmpty()) {

            if (photo.getSize() > MAX_PHOTO_SIZE) {
                throw new IllegalArgumentException("La photo ne doit pas dépasser 3 Mo");
            }

            String contentType = photo.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Le fichier envoyé doit être une image");
            }

            try {
                photoBytes = photo.getBytes();
                photoContentType = contentType;
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la lecture de la photo", e);
            }
        }

        User admin = User.builder()
                .nom(request.nom())
                .prenom(request.prenom())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .photo(photoBytes)
                .photoContentType(photoContentType)
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