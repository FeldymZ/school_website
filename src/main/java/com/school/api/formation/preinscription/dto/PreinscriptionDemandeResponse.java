package com.school.api.formation.preinscription.dto;

import com.school.api.formation.preinscription.entity.StatutDemande;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PreinscriptionDemandeResponse(

        Long id,

        /* ================= IDENTITE ================= */

        String civilite,

        String nom,
        String prenom,

        String dateNaissance,
        String lieuNaissance,

        String nationalite,

        /* ================= CONTACT ================= */

        String email,
        String telephone,
        String whatsapp,

        /* ================= FORMATION ================= */

        String niveau,
        String formation,

        /* ================= DIPLOME ================= */

        String diplomePresente,
        String statutDiplome,
        Integer anneeObtention,
        String etablissementProvenance,

        /* ================= AUTRES ================= */

        String anneeUniversitaire,

        StatutDemande statut,

        LocalDateTime createdAt,
        LocalDateTime validatedAt,

        /* ================= PDF ================= */

        String pdfUrl

) {}