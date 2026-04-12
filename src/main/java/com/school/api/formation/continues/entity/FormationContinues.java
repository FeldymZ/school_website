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
    @Column(name = "reference", unique = true, nullable = false)
    private Integer reference;

    /* =========================
       🔥 NOUVEAU : SLUG PUBLIC
       ========================= */
    @Column(name = "slug", unique = true, nullable = false, length = 150)
    private String slug;

    /* =========================
       INFOS
       ========================= */

    @Column(name = "titre", nullable = false)
    private String libelle;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "objectifs", columnDefinition = "TEXT")
    private String objectifs;

    @Column(name = "competences", columnDefinition = "TEXT")
    private String competences;

    @Column(name = "prix")
    private Double prix;

    @Column(name = "duree")
    private Integer duree;

    @Enumerated(EnumType.STRING)
    @Column(name = "unite_duree")
    private UniteDuree uniteDuree;

    @Column(name = "lieu")
    private String lieu;

    @Column(name = "titre_delivre")
    private String titreDelivre;

    @Column(name = "logo")
    private String logo;

    @Column(name = "enabled")
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