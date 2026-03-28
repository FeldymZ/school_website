package com.school.api.formation.continues.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SousCategorieDTO {

    private Long id;

    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;

    @NotNull(message = "La catégorie est obligatoire")
    private Long categorieId;

    private List<FormationDTO> formations;
}