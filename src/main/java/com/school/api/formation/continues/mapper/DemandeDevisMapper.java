package com.school.api.formation.continues.mapper;

import com.school.api.formation.continues.dto.*;
import com.school.api.formation.continues.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DemandeDevisMapper {

    /* =========================
       DEMANDE
       ========================= */
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
        dto.setDateTraitement(d.getDateTraitement());

        dto.setLignes(
                d.getLignes() == null ? List.of() :
                        d.getLignes().stream().map(l -> {
                            DemandeDevisAdminDTO.LigneDTO ligne = new DemandeDevisAdminDTO.LigneDTO();
                            ligne.setId(l.getId());
                            ligne.setFormationLibelle(l.getFormationLibelle());
                            ligne.setNombreParticipants(l.getNombreParticipants());
                            ligne.setPrix(l.getPrix());
                            ligne.setDuree(l.getDuree());
                            ligne.setUniteDuree(l.getUniteDuree() != null ? l.getUniteDuree().name() : null);
                            return ligne;
                        }).toList()
        );

        dto.setReponses(
                d.getReponses() == null ? List.of() :
                        d.getReponses().stream().map(this::toReponseDTO).toList()
        );

        dto.setDevis(d.getDevis() != null ? toDevisDTO(d.getDevis()) : null);

        return dto;
    }

    /* =========================
       RÉPONSE
       ========================= */
    public DemandeDevisReponseDTO toReponseDTO(DemandeDevisReponseContinues r) {
        return new DemandeDevisReponseDTO(
                r.getId(),
                r.getMessage(),
                r.getPieceJointeUrl(),
                r.getEnvoyePar(),
                r.getDateEnvoi()
        );
    }

    /* =========================
       DEVIS
       ========================= */
    public DevisDTO toDevisDTO(DevisFormationContinues v) {

        DevisDTO dto = new DevisDTO();

        dto.setId(v.getId());
        dto.setNumero(v.getNumero());
        dto.setDateEmission(v.getDateEmission());
        dto.setDateValidite(v.getDateValidite());

        dto.setMontantTotal(v.getMontantTotal());
        dto.setTypeReduction(v.getTypeReduction() != null ? v.getTypeReduction().name() : TypeReduction.AUCUNE.name());
        dto.setValeurReduction(v.getValeurReduction());
        dto.setMontantReduction(v.getMontantReduction() != null ? v.getMontantReduction() : 0.0);
        dto.setNetAPayer(v.getNetAPayer());

        dto.setConditions(v.getConditions());
        dto.setGenerePar(v.getGenerePar());
        dto.setDateGeneration(v.getDateGeneration());
        dto.setDateEnvoi(v.getDateEnvoi());

        dto.setEmetteurId(v.getEmetteur() != null ? v.getEmetteur().getId() : null);
        dto.setEmetteurNom(v.getEmetteurNom());
        dto.setEmetteurFonction(v.getEmetteurFonction());

        dto.setLignes(
                v.getLignes().stream().map(l -> new DevisDTO.LigneDTO(
                        l.getLigneDemandeId(),
                        l.getLibelle(),
                        l.getDuree(),
                        l.getUniteDuree(),
                        l.getNombreParticipants(),
                        l.getPrixUnitaire(),
                        l.getMontant()
                )).toList()
        );

        return dto;
    }
}