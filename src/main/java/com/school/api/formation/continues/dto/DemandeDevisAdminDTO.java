package com.school.api.formation.continues.dto;

import com.school.api.formation.continues.entity.StatutDemande;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DemandeDevisAdminDTO {

    private Long id;

    private String nomClient;
    private String email;
    private String telephone;

    private boolean entreprise;
    private String nomStructure;

    private LocalDateTime dateDemande; // 🔥 FIX
    private StatutDemande statut;
    private LocalDateTime dateTraitement;

    private List<LigneDemandeAdminDTO> lignes;
}