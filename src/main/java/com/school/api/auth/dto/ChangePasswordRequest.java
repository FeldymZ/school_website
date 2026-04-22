package com.school.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// ✅ FIX : DTO manquant, créé ici
public record ChangePasswordRequest(

        @NotBlank
        @Size(min = 8)
        String password

) {}