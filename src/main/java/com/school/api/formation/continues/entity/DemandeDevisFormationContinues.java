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

    /* ================= CLIENT ================= */
    private String nomClient;
    private String email;
    private String telephone;

    private boolean entreprise;
    private String nomStructure;

    /* ================= STATUT ================= */
    private LocalDateTime dateDemande;

    @Enumerated(EnumType.STRING)
    private StatutDemande statut = StatutDemande.PAS_ENCORE_TRAITEE;

    private LocalDateTime dateTraitement;

    /* ================= LIGNES ================= */
    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DemandeDevisLigneFormationContinues> lignes = new ArrayList<>();

    /* ================= REPONSES ================= */
    @JsonIgnore
    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL)
    private List<DemandeDevisReponseContinues> reponses;
}