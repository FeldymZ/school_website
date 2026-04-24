package com.school.api.formation.preinscription.dto;

import com.school.api.formation.preinscription.entity.StatutDemande;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PreinscriptionDemandeResponse(
        Long id,
        String civilite,
        String nom,
        String prenom,
        String email,
        String telephone,
        String whatsapp,
        String niveau,
        String formation,
        String nationalite,
        String anneeUniversitaire,
        StatutDemande statut,
        LocalDateTime createdAt,
        LocalDateTime validatedAt,
        String pdfUrl
) {}