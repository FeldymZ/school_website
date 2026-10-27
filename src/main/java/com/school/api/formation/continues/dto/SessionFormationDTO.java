package com.school.api.formation.continues.dto;

import java.time.LocalDate;

/** Session vue par le back office */
public record SessionFormationDTO(
        Long id,
        Long formationId,
        String formationLibelle,
        String formationSlug,
        LocalDate dateDebut,
        LocalDate dateFin,
        String lieu,
        String horaires,
        Integer nombrePlaces,
        long inscrits,          // en attente + confirmées
        long confirmees,
        Integer placesRestantes, // null = illimité
        boolean active,
        String etat              // A_VENIR, EN_COURS, TERMINEE
) {
}