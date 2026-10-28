package com.school.api.formation.continues.dto;

import java.time.LocalDateTime;

/** En-tête de la page publique « Sessions de formation » */
public record PageSessionsFormationDTO(
        String badge,
        String titre,
        String sousTitre,
        String image,                 // chemin de l'image (null = pas d'image)
        LocalDateTime dateModification,
        String modifiePar
) {
}