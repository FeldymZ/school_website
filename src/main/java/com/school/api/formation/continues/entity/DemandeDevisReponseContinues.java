package com.school.api.formation.continues.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "demande_devis_reponse_continues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandeDevisReponseContinues {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 4000)
  private String message;

  private String pieceJointeUrl;

  private String envoyePar;

  private LocalDateTime dateEnvoi;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "demande_id", nullable = false)
  private DemandeDevisFormationContinues demande;
}
