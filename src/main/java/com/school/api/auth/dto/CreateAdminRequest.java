package com.school.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateAdminRequest(

        @NotBlank
        String nom,

        @NotBlank
        String prenom,

        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 8)
        String password,

        // 🆕 optionnel — URL de la photo de profil (ex: lien Cloudinary, S3, etc.)
        @Pattern(
                regexp = "^$|^(https?://).+",
                message = "La photo de profil doit être une URL valide commençant par http:// ou https://"
        )
        String photoUrl,

        // optionnel — si absent, l'admin est créé sans accès menu (à configurer après coup)
        Set<String> menuAccess

) {}