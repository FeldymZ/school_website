package com.school.api.formation.continues.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DemandeDevisAdminDTO {

    private Long id;

    private String nomClient;
    private String email;
    private String telephone;

    private boolean entreprise;
    private String nomStructure;

    private LocalDateTime dateDemande;
    private String statut;
    private LocalDateTime dateTraitement;

    private List<LigneDTO> lignes;
    private List<DemandeDevisReponseDTO> reponses;

    /* null si aucun devis n'a été validé */
    private DevisDTO devis;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LigneDTO {
        private Long id;
        private String formationLibelle;
        private Integer nombreParticipants;
        private Double prix;
        private Integer duree;
        private String uniteDuree;
    }
}