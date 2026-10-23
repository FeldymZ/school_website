package com.school.api.formation.continues.dto;

import com.school.api.formation.continues.entity.TypeReduction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Données de validation d'un devis */
@Getter
@Setter
public class GenererDevisRequest {

    /* Validateur choisi (null → validateur par défaut) */
    private Long emetteurId;

    @Min(1) @Max(365)
    private Integer validiteJours;          // null → app.devis.validite-jours

    /* Réduction : AUCUNE (par défaut), TAUX ou MONTANT */
    private TypeReduction typeReduction = TypeReduction.AUCUNE;

    /* 10 pour 10 %, ou 50000 pour 50 000 XAF */
    @DecimalMin("0.0")
    private Double valeurReduction;

    @Size(max = 2000)
    private String conditions;

    /* Prix / participants modifiés par l'admin (facultatif) */
    @Valid
    private List<LigneOverride> lignes = new ArrayList<>();

    @Getter
    @Setter
    public static class LigneOverride {

        @NotNull
        private Long ligneId;

        @Min(1)
        private Integer nombreParticipants;

        @DecimalMin("0.0")
        private Double prixUnitaire;
    }
}