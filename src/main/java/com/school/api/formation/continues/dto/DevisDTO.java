package com.school.api.formation.continues.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DevisDTO {

    private Long id;
    private String numero;

    private LocalDate dateEmission;
    private LocalDate dateValidite;

    /* Montants */
    private Double montantTotal;
    private String typeReduction;     // AUCUNE, TAUX, MONTANT
    private Double valeurReduction;
    private Double montantReduction;
    private Double netAPayer;

    private String conditions;
    private String generePar;
    private LocalDateTime dateGeneration;
    private LocalDateTime dateEnvoi;

    /* Validateur */
    private Long emetteurId;
    private String emetteurNom;
    private String emetteurFonction;

    private List<LigneDTO> lignes;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LigneDTO {
        private Long ligneDemandeId;
        private String libelle;
        private Integer duree;
        private String uniteDuree;
        private Integer nombreParticipants;
        private Double prixUnitaire;
        private Double montant;
    }
}