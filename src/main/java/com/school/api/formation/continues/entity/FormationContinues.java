package com.school.api.formation.continues.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "formation_continues")
@Getter
@Setter
public class FormationContinues {

  /* =====================================
     ID
     ===================================== */

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /* =====================================
     INFORMATIONS PRINCIPALES
     ===================================== */

  @Column(nullable = false)
  private String titre;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String description;

  @Column(unique = true, nullable = false)
  private String slug;

  /* =====================================
     FICHIERS
     ===================================== */

  private String coverUrl;

  private String pdfUrl;

  /* =====================================
     STATUT
     ===================================== */

  private boolean enabled = true;

  /* =====================================
     RELATION DEMANDES DE DEVIS
     ===================================== */

  @OneToMany(
          mappedBy = "formation",
          cascade = CascadeType.ALL,
          orphanRemoval = true,
          fetch = FetchType.LAZY
  )
  @JsonIgnore
  private List<DemandeDevisFormationContinues> demandes;
}