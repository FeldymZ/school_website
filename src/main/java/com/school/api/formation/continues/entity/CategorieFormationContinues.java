package com.school.api.formation.continues.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "categorie_formation_continues")
@Getter
@Setter
public class CategorieFormationContinues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String libelle;

    @JsonIgnore // 🔥 IMPORTANT
    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL)
    private List<SousCategorieFormationContinues> sousCategories;
}