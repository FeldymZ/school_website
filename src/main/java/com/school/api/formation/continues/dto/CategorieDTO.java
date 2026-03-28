package com.school.api.formation.continues.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategorieDTO {

    private Long id;
    private String libelle;
    private List<SousCategorieDTO> sousCategories;
}