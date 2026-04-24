package com.school.api.formation.preinscription.dto;

import com.school.api.formation.preinscription.entity.Civilite;
import com.school.api.formation.preinscription.entity.NiveauSouhaite;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PreinscriptionDemandeRequest(

        @NotNull
        Civilite civilite,

        @NotBlank
        String nom,

        @NotBlank
        String prenom,

        @NotNull
        @Past
        LocalDate dateNaissance,

        @NotBlank
        String lieuNaissance,

        @NotBlank
        String nationalite,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String telephone,

        String whatsapp,

        @NotNull
        NiveauSouhaite niveauSouhaite,

        @NotNull
        Long formationId
) {}