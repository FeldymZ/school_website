package com.school.api.formation.preinscription.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SessionPublicResponse(
        boolean ouverte,
        String anneeUniversitaire,
        LocalDateTime dateDebut,
        LocalDateTime dateFin
) {}