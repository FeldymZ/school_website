package com.school.api.formation.continues.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private LocalDateTime dateDemande;

    @Enumerated(EnumType.STRING)
    private StatutDemande statut = StatutDemande.PAS_ENCORE_TRAITEE;

    private LocalDateTime dateTraitement;

    /* Une ligne par formation demandée */
    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<DemandeDevisLigneFormationContinues> lignes = new ArrayList<>();

    /* Historique des réponses envoyées au client */
    @JsonIgnore
    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dateEnvoi ASC")
    private List<DemandeDevisReponseContinues> reponses = new ArrayList<>();

    /* Devis validé (supprimé automatiquement avec la demande) */
    @JsonIgnore
    @OneToOne(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    private DevisFormationContinues devis;
}