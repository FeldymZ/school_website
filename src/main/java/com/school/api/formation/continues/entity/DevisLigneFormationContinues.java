package com.school.api.formation.continues.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "devis_ligne_formation_continues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DevisLigneFormationContinues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer ordre;

    /* Ligne de la demande d'origine */
    private Long ligneDemandeId;

    /* SNAPSHOT au moment de la validation */
    private String libelle;
    private Integer duree;
    private String uniteDuree;
    private Integer nombreParticipants;
    private Double prixUnitaire;
    private Double montant;

    @ManyToOne
    @JoinColumn(name = "devis_id", nullable = false)
    private DevisFormationContinues devis;
}