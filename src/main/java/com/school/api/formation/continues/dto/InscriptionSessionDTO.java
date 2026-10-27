package com.school.api.formation.continues.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Demande de participation vue par le back office */
public record InscriptionSessionDTO(
        Long id,
        Long sessionId,
        String formationLibelle,
        LocalDate sessionDateDebut,
        LocalDate sessionDateFin,
        String sessionLieu,
        String nom,
        String prenom,
        String sexe,
        LocalDate dateNaissance,
        String lieuNaissance,
        String email,
        String telephone,
        String lieuResidence,
        boolean enActivite,
        String profession,
        String statut,
        String motifRefus,
        LocalDateTime dateInscription,
        LocalDateTime dateTraitement,
        String traitePar
) {
}