package com.school.api.formation.continues.mapper;

import com.school.api.formation.continues.dto.*;
import com.school.api.formation.continues.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DemandeDevisMapper {

    public DemandeDevisAdminDTO toDTO(DemandeDevisFormationContinues d) {

        DemandeDevisAdminDTO dto = new DemandeDevisAdminDTO();

        dto.setId(d.getId());
        dto.setNomClient(d.getNomClient());
        dto.setEmail(d.getEmail());
        dto.setTelephone(d.getTelephone());

        dto.setEntreprise(d.isEntreprise());
        dto.setNomStructure(d.getNomStructure());

        dto.setDateDemande(d.getDateDemande());
        dto.setStatut(d.getStatut().name());

        /* ================= LIGNES ================= */

        dto.setLignes(
                d.getLignes() != null
                        ? d.getLignes().stream().map(l -> {

                    DemandeDevisAdminDTO.LigneDTO ligne =
                            new DemandeDevisAdminDTO.LigneDTO();

                    ligne.setFormationLibelle(l.getFormationLibelle());
                    ligne.setNombreParticipants(l.getNombreParticipants());
                    ligne.setPrix(l.getPrix());
                    ligne.setDuree(l.getDuree());
                    ligne.setUniteDuree(
                            l.getUniteDuree() != null
                                    ? l.getUniteDuree().name()
                                    : null
                    );

                    return ligne;

                }).collect(Collectors.toList())
                        : List.of()
        );

        /* ================= REPONSES ================= */

        dto.setReponses(
                d.getReponses() != null
                        ? d.getReponses().stream().map(r ->
                        new DemandeDevisReponseDTO(
                                r.getId(),
                                r.getMessage(),
                                r.getPieceJointeUrl(),
                                r.getEnvoyePar().name(),
                                r.getDateEnvoi()
                        )
                ).collect(Collectors.toList())
                        : List.of()
        );

        return dto;
    }
}