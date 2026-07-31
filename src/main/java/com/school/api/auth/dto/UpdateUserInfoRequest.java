package com.school.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserInfoRequest(

        @NotBlank
        String nom,

        @NotBlank
        String prenom,

        @Email
        @NotBlank
        String email,

        boolean removePhoto // true => supprime la photo existante (ignoré si une nouvelle photo est envoyée)

) {}