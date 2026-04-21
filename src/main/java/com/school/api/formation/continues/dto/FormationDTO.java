package com.school.api.formation.continues.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormationDTO {

    private Long id;
    private Integer reference;
    private String slug;

    private String libelle;
    private String description;

    private String objectifs;
    private String competences;

    private Double prix;

    // ✅ NOUVEAU
    private boolean afficherPrix;

    private Integer duree;
    private String uniteDuree;

    private String coverUrl;
    private boolean enabled;

    private SousCategorieDTO sousCategorie;
}