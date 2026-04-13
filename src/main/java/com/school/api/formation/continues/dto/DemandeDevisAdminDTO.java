package com.school.api.formation.continues.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DemandeDevisAdminDTO {

    private Long id;

    private String nomClient;
    private String email;
    private String telephone;

    private boolean entreprise;
    private String nomStructure;

    private LocalDateTime dateDemande;
    private String statut; // ✅ STRING
    private LocalDateTime dateTraitement;

    private List<LigneDTO> lignes;
    private List<DemandeDevisReponseDTO> reponses;

    /* ================= LIGNES ================= */

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LigneDTO {

        private String formationLibelle;
        private Integer nombreParticipants;

        private Double prix;
        private Integer duree;
        private String uniteDuree;
    }
}