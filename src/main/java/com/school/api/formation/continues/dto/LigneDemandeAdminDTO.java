package com.school.api.formation.continues.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LigneDemandeAdminDTO {

    private String formationLibelle;
    private Double prix;
    private Integer duree;
    private String uniteDuree;
    private Integer nombreParticipants;
}