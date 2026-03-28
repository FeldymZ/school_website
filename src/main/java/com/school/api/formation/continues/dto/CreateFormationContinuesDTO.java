package com.school.api.formation.continues.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateFormationContinuesDTO {

    @NotBlank
    private String libelle;

    @NotBlank
    private String description;

    private String objectifs;

    private String competences;

    @NotNull
    private Double prix;

    @NotNull
    private Integer duree;

    @NotNull
    private String uniteDuree; // JOURS / MOIS / ANNEES

    private String lieu;

    private String titreDelivre;

    private MultipartFile cover;
}