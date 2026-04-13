package com.school.api.formation.continues.service;

import com.school.api.formation.continues.dto.CreateDemandeDevisContinuesDTO;
import com.school.api.formation.continues.dto.CreateDemandeDevisGlobalDTO;
import com.school.api.formation.continues.entity.*;
import com.school.api.formation.continues.repository.DemandeDevisFormationContinuesRepository;
import com.school.api.formation.continues.repository.FormationContinuesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class DemandeDevisContinuesPublicService {

    private final DemandeDevisFormationContinuesRepository demandeRepository;
    private final FormationContinuesRepository formationRepository;

    /* =========================
       DEMANDE CLASSIQUE
       ========================= */
    public void create(CreateDemandeDevisContinuesDTO dto) {

        if (dto.getLignes() == null || dto.getLignes().isEmpty()) {
            throw new RuntimeException("Panier vide");
        }

        if (dto.isEntreprise() &&
                (dto.getNomStructure() == null || dto.getNomStructure().isBlank())) {
            throw new RuntimeException("Nom de structure obligatoire");
        }

        DemandeDevisFormationContinues demande = new DemandeDevisFormationContinues();

        demande.setNomClient(dto.getNomClient());
        demande.setEmail(dto.getEmail());
        demande.setTelephone(dto.getTelephone());
        demande.setEntreprise(dto.isEntreprise());
        demande.setNomStructure(dto.getNomStructure());
        demande.setDateDemande(LocalDateTime.now());
        demande.setStatut(StatutDemande.PAS_ENCORE_TRAITEE);

        List<DemandeDevisLigneFormationContinues> lignes = dto.getLignes()
                .stream()
                .map(l -> {

                    FormationContinues f = formationRepository.findBySlug(l.getSlug())
                            .orElseThrow(() ->
                                    new RuntimeException("Formation introuvable : " + l.getSlug())
                            );

                    if (!f.isEnabled()) {
                        throw new RuntimeException("Formation désactivée : " + l.getSlug());
                    }

                    if (l.getNombreParticipants() <= 0) {
                        throw new RuntimeException("Nombre de participants invalide");
                    }

                    DemandeDevisLigneFormationContinues ligne = new DemandeDevisLigneFormationContinues();

                    ligne.setFormation(f);
                    ligne.setFormationLibelle(f.getLibelle());
                    ligne.setPrix(f.getPrix());
                    ligne.setDuree(f.getDuree());
                    ligne.setUniteDuree(f.getUniteDuree());
                    ligne.setNombreParticipants(l.getNombreParticipants());
                    ligne.setDemande(demande);

                    return ligne;
                })
                .toList();

        demande.setLignes(lignes);

        demandeRepository.save(demande);
    }

    /* =========================
       DEMANDE GLOBALE (PANIER)
       ========================= */
    public void createGlobal(CreateDemandeDevisGlobalDTO dto) {

        if (dto.getFormations() == null || dto.getFormations().isEmpty()) {
            throw new RuntimeException("Panier vide");
        }

        if (dto.isEntreprise() &&
                (dto.getNomStructure() == null || dto.getNomStructure().isBlank())) {
            throw new RuntimeException("Nom de structure obligatoire");
        }

        CreateDemandeDevisContinuesDTO demande = new CreateDemandeDevisContinuesDTO();

        demande.setNomClient(dto.getNomClient());
        demande.setEmail(dto.getEmail());
        demande.setTelephone(dto.getTelephone());
        demande.setEntreprise(dto.isEntreprise());
        demande.setNomStructure(dto.getNomStructure());

        demande.setLignes(
                dto.getFormations().stream().map(f -> {

                    if (f.getParticipants() <= 0) {
                        throw new RuntimeException("Nombre de participants invalide");
                    }

                    CreateDemandeDevisContinuesDTO.LigneDemandeDTO ligne =
                            new CreateDemandeDevisContinuesDTO.LigneDemandeDTO();

                    ligne.setSlug(f.getSlug());
                    ligne.setNombreParticipants(f.getParticipants());

                    return ligne;

                }).toList()
        );

        create(demande);
    }
}