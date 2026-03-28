package com.school.api.formation.continues.service;

import com.school.api.formation.continues.dto.*;
import com.school.api.formation.continues.entity.*;
import com.school.api.formation.continues.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemandeDevisContinuesPublicService {

    private final DemandeDevisFormationContinuesRepository demandeRepository;
    private final FormationContinuesRepository formationRepository;

    public void create(CreateDemandeDevisContinuesDTO dto) {

        if (dto.getLignes().isEmpty()) {
            throw new RuntimeException("Panier vide");
        }

        DemandeDevisFormationContinues demande = new DemandeDevisFormationContinues();

        demande.setNomClient(dto.getNomClient());
        demande.setEmail(dto.getEmail());
        demande.setTelephone(dto.getTelephone());
        demande.setEntreprise(dto.isEntreprise());
        demande.setNomStructure(dto.getNomStructure());
        demande.setDateDemande(LocalDate.now());

        List<DemandeDevisLigneFormationContinues> lignes = new ArrayList<>();

        for (LigneDemandeDTO l : dto.getLignes()) {

            FormationContinues f = formationRepository.findById(l.getFormationId())
                    .orElseThrow(() -> new RuntimeException("Formation introuvable"));

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
}