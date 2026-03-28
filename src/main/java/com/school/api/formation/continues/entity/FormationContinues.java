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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =========================
       REFERENCE UNIQUE
       ========================= */
    @Column(unique = true, nullable = false)
    private Integer reference;

    /* =========================
       INFOS
       ========================= */
    @Column(nullable = false)
    private String libelle;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String objectifs;

    @Column(columnDefinition = "TEXT")
    private String competences;

    private Double prix;

    private Integer duree;

    @Enumerated(EnumType.STRING)
    private UniteDuree uniteDuree;

    private String lieu;

    private String titreDelivre;

    private String logo;

    private boolean enabled = true;

    /* =========================
       RELATION
       ========================= */
    @ManyToOne
    @JoinColumn(name = "sous_categorie_id", nullable = false)
    private SousCategorieFormationContinues sousCategorie;

    @JsonIgnore
    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL)
    private List<DemandeDevisLigneFormationContinues> lignes;
}