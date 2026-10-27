package com.school.api.formation.continues.dto;

import java.time.LocalDate;

/** Formulaire d'inscription du site public */
public record InscriptionSessionRequest(
        String nom,
        String prenom,
        String sexe,            // "M" ou "F"
        LocalDate dateNaissance,
        String lieuNaissance,
        String email,
        String telephone,
        String lieuResidence,
        Boolean enActivite,
        String profession       // obligatoire si enActivite = true
) {
}