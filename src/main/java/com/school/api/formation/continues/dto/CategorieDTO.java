package com.school.api.formation.continues.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategorieDTO {

    private Long id;

    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;

    private List<SousCategorieDTO> sousCategories;
}