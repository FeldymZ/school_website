package com.school.api.formation.continues.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "sous_categorie_formation_continues")
@Getter
@Setter
public class SousCategorieFormationContinues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String libelle;

    @ManyToOne
    @JoinColumn(name = "categorie_id", nullable = false)
    private CategorieFormationContinues categorie;

    @OneToMany(mappedBy = "sousCategorie", cascade = CascadeType.ALL)
    private List<FormationContinues> formations;
}