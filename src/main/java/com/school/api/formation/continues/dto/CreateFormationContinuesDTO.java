package com.school.api.formation.continues.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateFormationContinuesDTO {

    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    private String objectifs;
    private String competences;

    @Positive(message = "Le prix doit être positif")
    private Double prix;

    @Positive(message = "La durée doit être positive")
    private Integer duree;

    private String uniteDuree;

    private String lieu;
    private String titreDelivre;

    private MultipartFile cover;

    // 🔥 AJOUT CRITIQUE
    private Long sousCategorieId;
}