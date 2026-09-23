package com.school.api.formation.preinscription.dto;

import com.school.api.formation.preinscription.entity.StatutDemande;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreinscriptionDemandeResponse {

    private Long id;

    /* ================= IDENTITE ================= */

    private String civilite;
    private String nom;
    private String prenom;
    private String dateNaissance;
    private String lieuNaissance;
    private String nationalite;

    /* ================= CONTACT ================= */

    private String email;
    private String telephone;
    private String whatsapp;

    /* ================= FORMATION ================= */

    private String niveau;
    private String formation;

    /* ================= DIPLOME ================= */

    private String diplomePresente;
    private String statutDiplome;
    private Integer anneeObtention;
    private String etablissementProvenance;

    /* ================= AUTRES ================= */

    private String anneeUniversitaire;

    private StatutDemande statut;

    private LocalDateTime createdAt;
    private LocalDateTime validatedAt;
    private LocalDateTime rejectedAt;

    /* ================= REJET ================= */

    private String motifRejet;

    /* ================= PDF ================= */

    private String pdfUrl;
}