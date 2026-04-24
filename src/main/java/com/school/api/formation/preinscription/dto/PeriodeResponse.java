package com.school.api.formation.preinscription.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PeriodeResponse(
        Long id,
        String annee,
        Long emetteurId,
        String emetteurNom,
        LocalDateTime dateDebut,
        LocalDateTime dateFin
) {}