package com.school.api.formation.continues.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class FormationContinues {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String titre;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(unique = true)
  private String slug;

  private String coverUrl;

  private String pdfUrl;

  private boolean enabled;

  /* ==============================
     RELATION DEMANDES DEVIS
     ============================== */

  @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL)
  @JsonIgnore
  private List<DemandeDevisFormationContinues> demandes;
}
