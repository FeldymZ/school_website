package com.school.api.auth.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record UserResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        boolean hasPhoto, // 🆕 indique si une photo existe ; à charger via GET /api/admin/users/{id}/photo
        String role,
        boolean enabled,
        Set<String> menuAccess
) {}