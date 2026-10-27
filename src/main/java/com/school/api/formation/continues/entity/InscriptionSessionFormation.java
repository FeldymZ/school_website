package com.school.api.formation.continues.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Demande de participation d'une personne à une session de formation.
 *
 * statut et sexe sont stockés en texte simple (pas d'enum en base)
 * pour éviter les contraintes CHECK que Hibernate ne met jamais à jour.
 */
@Entity
@Table(name = "inscription_session_formation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionSessionFormation {

    public static final String EN_ATTENTE = "EN_ATTENTE";
    public static final String CONFIRMEE = "CONFIRMEE";
    public static final String REFUSEE = "REFUSEE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private SessionFormationContinues session;

    /* ===== Identité ===== */
    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    /** "M" ou "F" */
    @Column(nullable = false, length = 1)
    private String sexe;

    @Column(nullable = false)
    private LocalDate dateNaissance;

    @Column(nullable = false, length = 120)
    private String lieuNaissance;

    /* ===== Contact ===== */
    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 30)
    private String telephone;

    @Column(nullable = false, length = 150)
    private String lieuResidence;

    /* ===== Situation ===== */
    @Column(nullable = false)
    private Boolean enActivite;

    /** Renseignée si enActivite = true */
    @Column(length = 150)
    private String profession;

    /* ===== Traitement ===== */
    /** EN_ATTENTE, CONFIRMEE ou REFUSEE */
    @Column(nullable = false, length = 20)
    private String statut;

    @Column(length = 1000)
    private String motifRefus;

    @Column(nullable = false)
    private LocalDateTime dateInscription;

    private LocalDateTime dateTraitement;

    @Column(length = 150)
    private String traitePar;
}