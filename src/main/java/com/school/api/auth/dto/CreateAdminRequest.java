package com.school.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

        // optionnel — si absent, l'admin est créé sans accès menu (à configurer après coup)
        Set<String> menuAccess

) {}