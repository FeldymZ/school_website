package com.school.api.formation.continues.dto;

import java.time.LocalDate;

/** Session vue par le site public */
public record SessionPublicDTO(
        Long id,
        LocalDate dateDebut,
        LocalDate dateFin,
        String lieu,
        String horaires,
        Integer nombrePlaces,
        Integer placesRestantes, // null = illimité
        boolean complete
) {
}