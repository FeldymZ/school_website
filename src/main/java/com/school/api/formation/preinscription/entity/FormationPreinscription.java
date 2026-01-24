package com.school.api.formation.preinscription.entity;

import com.school.api.formation.initiale.entity.FormationInitiale;
import com.school.api.formation.initiale.entity.FormationInitialeLevel;
import com.school.api.formation.preinscription.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "formation_preinscriptions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormationPreinscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /* =========================
     INFORMATIONS PERSONNELLES
     ========================= */

  @Column(nullable = false)
  private String nom;

  @Column(nullable = false)
  private String prenom;

  @Column(nullable = false)
  private LocalDate dateNaissance;

  @Column(nullable = false)
  private String lieuNaissance;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Sexe sexe;

  @Column(nullable = false)
  private String nationalite;

  @Column(nullable = false)
  private String adresse;

  @Column(nullable = false)
  private String telephone;

  @Column(nullable = false)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SituationFamiliale situationFamiliale;

  /* =========================
     ÉTABLISSEMENT D’ORIGINE
     ========================= */

  @Column(nullable = false)
  private String nomEtablissement;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TypeEtablissement typeEtablissement;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SerieBaccalaureat serieBaccalaureat;

  @Column(nullable = false)
  private Integer anneeObtention;

  /* =========================
     FORMATION SOUHAITÉE
     ========================= */

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "formation_id", nullable = false)
  private FormationInitiale formation;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FormationInitialeLevel niveau; // LICENCE / MASTER

  @Column(nullable = false)
  private Integer niveauEtude; // 1–3 ou 1–2

  /* =========================
     STATUT ÉTUDIANT
     ========================= */

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatutEtudiant statutEtudiant;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ModeFinancement modeFinancement;

  private String autreFinancement;

  /* =========================
     PARENT / TUTEUR
     ========================= */

  @Column(nullable = false)
  private String profession; // champ libre

  /* =========================
     WORKFLOW ADMIN
     ========================= */

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatutPreinscription statut;

  private String commentaireAdmin;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  private LocalDateTime decisionAt;
}
