package com.school.api.formation.continues.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SousCategorieDTO {

    private Long id;
    private String libelle;

    private Long categorieId; // 🔥 IMPORTANT

    private List<FormationDTO> formations;
}