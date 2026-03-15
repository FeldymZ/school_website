package com.school.api.formation.continues.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties({"reponses"})
@Entity
@Table(name = "demande_devis_formation_continues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandeDevisFormationContinues {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nomClient;
  private String email;
  private String telephone;

  private boolean entreprise;
  private String nomStructure;

  private Integer nombreParticipants;

  private Integer dureeSouhaitee;

  @Enumerated(EnumType.STRING)
  private UniteDuree uniteDuree;

  private LocalDate dateDemande;

  @Enumerated(EnumType.STRING)
  private StatutDemande statut = StatutDemande.PAS_ENCORE_TRAITEE;

  private LocalDateTime dateTraitement;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "formation_id", nullable = false)
  private FormationContinues formation;

  @JsonIgnore
  @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL)
  private List<DemandeDevisReponseContinues> reponses;
}