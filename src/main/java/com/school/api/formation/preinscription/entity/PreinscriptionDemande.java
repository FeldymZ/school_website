package com.school.api.formation.preinscription.entity;

import com.school.api.formation.initiale.entity.FormationInitiale;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "preinscription_demandes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreinscriptionDemande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ================= IDENTITE ================= */

    @Enumerated(EnumType.STRING)
    private Civilite civilite;

    private String nom;
    private String prenom;

    private LocalDate dateNaissance;
    private String lieuNaissance;

    private String nationalite;

    /* ================= CONTACT ================= */

    private String email;
    private String telephone;
    private String whatsapp;

    /* ================= FORMATION ================= */

    @Enumerated(EnumType.STRING)
    private NiveauSouhaite niveauSouhaite;

    /* ===== DIPLOME ===== */

    private String diplomePresente;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutDiplome statutDiplome = StatutDiplome.EN_COURS;

    private Integer anneeObtention;

    private String etablissementProvenance;

    @ManyToOne(fetch = FetchType.LAZY)
    private FormationInitiale formation;

    /* ================= PERIODE ================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periode_id", nullable = false)
    private PreinscriptionPeriode periode;

    /* ================= STATUT ================= */

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    /* ================= DATES ================= */

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime validatedAt;

    /* ================= PDF ================= */

    private String pdfUrl;
}