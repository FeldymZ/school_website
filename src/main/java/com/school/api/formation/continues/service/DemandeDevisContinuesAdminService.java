package com.school.api.formation.continues.service;

import com.school.api.common.storage.FileStorageService;
import com.school.api.formation.continues.dto.*;
import com.school.api.formation.continues.entity.*;
import com.school.api.formation.continues.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DemandeDevisContinuesAdminService {

    private final DemandeDevisFormationContinuesRepository repository;
    private final DemandeDevisReponseContinuesRepository reponseRepository;
    private final FileStorageService fileStorageService;

    public Page<DemandeDevisAdminDTO> getAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size))
                .map(this::mapToDTO);
    }

    public void repondre(Long demandeId, RepondreDemandeDTO dto) {

        DemandeDevisFormationContinues demande = repository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));

        DemandeDevisReponseContinues reponse = new DemandeDevisReponseContinues();

        reponse.setDemande(demande);
        reponse.setMessage(dto.getMessage());
        reponse.setEnvoyePar("ADMIN");
        reponse.setDateEnvoi(LocalDateTime.now());

        if (dto.getPieceJointe() != null && !dto.getPieceJointe().isEmpty()) {
            reponse.setPieceJointeUrl(
                    fileStorageService.store(dto.getPieceJointe())
            );
        }

        reponseRepository.save(reponse);

        /* =========================
           MAJ STATUT
           ========================= */
        demande.setStatut(StatutDemande.TRAITEE);
        demande.setDateTraitement(LocalDateTime.now());

        repository.save(demande);
    }

    private DemandeDevisAdminDTO mapToDTO(DemandeDevisFormationContinues d) {

        DemandeDevisAdminDTO dto = new DemandeDevisAdminDTO();

        dto.setId(d.getId());
        dto.setNomClient(d.getNomClient());
        dto.setEmail(d.getEmail());
        dto.setTelephone(d.getTelephone());
        dto.setEntreprise(d.isEntreprise());
        dto.setNomStructure(d.getNomStructure());
        dto.setDateDemande(d.getDateDemande());
        dto.setStatut(d.getStatut());
        dto.setDateTraitement(d.getDateTraitement());

        dto.setLignes(
                d.getLignes().stream().map(l -> {
                    LigneDemandeAdminDTO ligne = new LigneDemandeAdminDTO();
                    ligne.setFormationLibelle(l.getFormationLibelle());
                    ligne.setNombreParticipants(l.getNombreParticipants());
                    ligne.setPrix(l.getPrix());
                    ligne.setDuree(l.getDuree());
                    ligne.setUniteDuree(
                            l.getUniteDuree() != null ? l.getUniteDuree().name() : null
                    );
                    return ligne;
                }).toList()
        );

        return dto;
    }
}