package com.school.api.formation.continues.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormationDTO {

    private Long id; // ADMIN ONLY

    private Integer reference;

    private String slug;

    /* 🔥 ALIGNÉ FRONT */
    private String titre;

    private String description;

    private String objectifs;
    private String competences;

    private Double prix;

    private Integer duree;
    private String uniteDuree;

    /* 🔥 ALIGNÉ FRONT */
    private String coverUrl;

    private boolean enabled;

    private SousCategorieDTO sousCategorie;
}