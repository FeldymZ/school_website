package com.school.api.formation.preinscription.dto;

import lombok.Builder;

@Builder
public record PeriodePublicResponse(
        boolean ouverte,
        String anneeUniversitaire
) {}