package com.school.api.formation.continues.service;

import com.school.api.formation.continues.dto.CreateDemandeDevisContinuesDTO;
import com.school.api.formation.continues.dto.LigneDemandeDTO;
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

        /* ================= VALIDATION ================= */

        if (dto.getLignes() == null || dto.getLignes().isEmpty()) {
            throw new RuntimeException("Panier vide");
        }

        if (dto.isEntreprise() &&
                (dto.getNomStructure() == null || dto.getNomStructure().isBlank())) {
            throw new RuntimeException(
                    "Le nom de la structure est obligatoire pour une entreprise"
            );
        }

        /* ================= CREATION ================= */

        DemandeDevisFormationContinues demande = new DemandeDevisFormationContinues();

        demande.setNomClient(dto.getNomClient());
        demande.setEmail(dto.getEmail());
        demande.setTelephone(dto.getTelephone());
        demande.setEntreprise(dto.isEntreprise());
        demande.setNomStructure(dto.getNomStructure());

        demande.setDateDemande(LocalDateTime.now()); // 🔥 FIX
        demande.setStatut(StatutDemande.PAS_ENCORE_TRAITEE); // 🔥 FIX

        /* ================= LIGNES ================= */

        List<DemandeDevisLigneFormationContinues> lignes = new ArrayList<>();

        for (LigneDemandeDTO l : dto.getLignes()) {

            if (l.getNombreParticipants() <= 0) {
                throw new RuntimeException("Nombre de participants invalide");
            }

            FormationContinues f = formationRepository.findById(l.getFormationId())
                    .orElseThrow(() ->
                            new RuntimeException("Formation introuvable : " + l.getFormationId())
                    );

            if (!f.isEnabled()) {
                throw new RuntimeException("Formation non disponible : " + f.getLibelle());
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

        /* ================= SAVE ================= */

        demandeRepository.save(demande);
    }
}