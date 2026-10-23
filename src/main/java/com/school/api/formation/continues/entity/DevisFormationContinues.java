package com.school.api.formation.continues.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devis_formation_continues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DevisFormationContinues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* DEV-2026-0001 — conservé en cas de revalidation */
    @Column(nullable = false, unique = true, length = 30)
    private String numero;

    @OneToOne
    @JoinColumn(name = "demande_id", nullable = false, unique = true)
    private DemandeDevisFormationContinues demande;

    private LocalDate dateEmission;
    private LocalDate dateValidite;

    /* ================= MONTANTS ================= */

    /* Somme des lignes */
    private Double montantTotal;

    /* Réduction : AUCUNE, TAUX (%) ou MONTANT (XAF) */
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private TypeReduction typeReduction = TypeReduction.AUCUNE;

    /* Valeur saisie : 10 (pour 10 %) ou 50000 (pour 50 000 XAF) */
    private Double valeurReduction;

    /* Montant réellement déduit, en XAF */
    private Double montantReduction;

    /* Total - réduction */
    private Double netAPayer;

    @Column(length = 2000)
    private String conditions;

    private String generePar;
    private LocalDateTime dateGeneration;

    /* Dernier envoi au client (remis à null si le devis est revalidé) */
    private LocalDateTime dateEnvoi;

    /* ================= VALIDATEUR ================= */

    @ManyToOne
    @JoinColumn(name = "emetteur_id")
    private DevisEmetteur emetteur;

    /* SNAPSHOT : le devis reste identique même si le validateur est modifié */
    private String emetteurNom;
    private String emetteurFonction;
    private String emetteurSignatureUrl;
    private String emetteurCachetUrl;

    /* Une ligne par formation */
    @OneToMany(mappedBy = "devis", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<DevisLigneFormationContinues> lignes = new ArrayList<>();
}