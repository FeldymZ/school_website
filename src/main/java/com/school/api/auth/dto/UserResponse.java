package com.school.api.auth.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record UserResponse(
        Long id,
        String nom,      // 🆕
        String prenom,   // 🆕
        String email,
        String photoUrl, // 🆕
        String role,
        boolean enabled,
        Set<String> menuAccess
) {}