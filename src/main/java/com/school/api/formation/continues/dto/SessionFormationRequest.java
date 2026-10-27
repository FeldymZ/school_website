package com.school.api.formation.continues.dto;

import java.time.LocalDate;

/** Création / modification d'une session */
public record SessionFormationRequest(
        Long formationId,
        LocalDate dateDebut,
        LocalDate dateFin,
        String lieu,
        String horaires,
        Integer nombrePlaces,
        Boolean active
) {
}