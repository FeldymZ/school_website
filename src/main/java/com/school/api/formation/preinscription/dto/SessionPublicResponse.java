package com.school.api.formation.preinscription.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record SessionPublicResponse(
        String anneeUniversitaire,
        boolean ouverte,
        LocalDateTime dateDebut,
        LocalDateTime dateFin
) {}