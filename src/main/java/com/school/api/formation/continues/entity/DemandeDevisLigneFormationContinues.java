package com.school.api.formation.continues.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "demande_devis_ligne_formation_continues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandeDevisLigneFormationContinues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer nombreParticipants;

    /* =========================
       SNAPSHOT
       ========================= */
    private String formationLibelle;
    private Double prix;
    private Integer duree;

    @Enumerated(EnumType.STRING)
    private UniteDuree uniteDuree;

    @ManyToOne
    @JoinColumn(name = "formation_id", nullable = false)
    private FormationContinues formation;

    @ManyToOne
    @JoinColumn(name = "demande_id", nullable = false)
    private DemandeDevisFormationContinues demande;
}