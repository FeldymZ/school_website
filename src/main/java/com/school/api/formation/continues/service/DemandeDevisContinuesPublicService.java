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

        List<DemandeDevisLigneFormationContinues> lignes = new ArrayList<>();

        for (CreateDemandeDevisContinuesDTO.LigneDemandeDTO l : dto.getLignes()) {

            FormationContinues f = formationRepository.findBySlug(l.getSlug());

            if (f == null || !f.isEnabled()) {
                throw new RuntimeException("Formation invalide : " + l.getSlug());
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

            lignes.add(ligne);
        }

        demande.setLignes(lignes);

        demandeRepository.save(demande);
    }

    public void createGlobal(CreateDemandeDevisGlobalDTO dto) {

        if (dto.getFormations() == null || dto.getFormations().isEmpty()) {
            throw new RuntimeException("Panier vide");
        }

        if (dto.isEntreprise() &&
                (dto.getNomStructure() == null || dto.getNomStructure().isBlank())) {
            throw new RuntimeException("Nom de structure obligatoire");
        }

        // 🔥 on construit UN SEUL DTO avec plusieurs lignes
        CreateDemandeDevisContinuesDTO demande = new CreateDemandeDevisContinuesDTO();

        demande.setNomClient(dto.getNomClient());
        demande.setEmail(dto.getEmail());
        demande.setTelephone(dto.getTelephone());
        demande.setEntreprise(dto.isEntreprise());
        demande.setNomStructure(dto.getNomStructure());

        List<CreateDemandeDevisContinuesDTO.LigneDemandeDTO> lignes = new ArrayList<>();

        for (CreateDemandeDevisGlobalDTO.DemandeDevisItemDTO f : dto.getFormations()) {

            if (f.getParticipants() <= 0) {
                throw new RuntimeException("Nombre de participants invalide");
            }

            CreateDemandeDevisContinuesDTO.LigneDemandeDTO ligne =
                    new CreateDemandeDevisContinuesDTO.LigneDemandeDTO();

            ligne.setSlug(f.getSlug());
            ligne.setNombreParticipants(f.getParticipants());

            lignes.add(ligne);
        }

        demande.setLignes(lignes);

        // 🔥 UNE SEULE DEMANDE avec plusieurs formations
        create(demande);
    }
}