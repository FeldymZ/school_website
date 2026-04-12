package com.school.api.formation.continues.service;

import com.school.api.common.storage.FileStorageService;
import com.school.api.formation.continues.dto.*;
import com.school.api.formation.continues.entity.*;
import com.school.api.formation.continues.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemandeDevisContinuesAdminService {

    private final DemandeDevisFormationContinuesRepository repository;
    private final DemandeDevisReponseContinuesRepository reponseRepository;
    private final FileStorageService fileStorageService;

    /* =========================
       📄 LISTE PAGINÉE
       ========================= */
    public Page<DemandeDevisAdminDTO> getAll(int page, int size) {
        return repository.findAll(
                        PageRequest.of(
                                page,
                                size,
                                Sort.by(Sort.Direction.DESC, "dateDemande")
                        )
                )
                .map(this::mapToDTO);
    }

    /* =========================
       🔍 DETAIL D'UNE DEMANDE
       ========================= */
    public DemandeDevisAdminDTO getById(Long id) {
        DemandeDevisFormationContinues demande = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));

        return mapToDTO(demande);
    }

    /* =========================
       ✉️ RÉPONDRE À UNE DEMANDE
       ========================= */
    @Transactional
    public void repondre(Long demandeId, RepondreDemandeDTO dto) {

        DemandeDevisFormationContinues demande = repository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));

        if (demande.getStatut() == StatutDemande.TRAITEE) {
            throw new RuntimeException("Cette demande a déjà été traitée");
        }

        DemandeDevisReponseContinues reponse = new DemandeDevisReponseContinues();

        reponse.setDemande(demande);
        reponse.setMessage(dto.getMessage());
        reponse.setEnvoyePar("ADMIN");
        reponse.setDateEnvoi(LocalDateTime.now());

        if (dto.getPieceJointe() != null && !dto.getPieceJointe().isEmpty()) {

            String fileUrl = fileStorageService
                    .storeDevisContinuesAttachment(dto.getPieceJointe());

            reponse.setPieceJointeUrl(fileUrl);
        }

        reponseRepository.save(reponse);

        /* 🔄 MAJ STATUT */
        demande.setStatut(StatutDemande.TRAITEE);
        demande.setDateTraitement(LocalDateTime.now());

        repository.save(demande);
    }

    /* =========================
       📩 HISTORIQUE DES RÉPONSES
       ========================= */
    public List<DemandeDevisReponseDTO> getReponses(Long demandeId) {

        DemandeDevisFormationContinues demande = repository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));

        return reponseRepository
                .findByDemandeIdOrderByDateEnvoiAsc(demande.getId())
                .stream()
                .map(r -> new DemandeDevisReponseDTO(
                        r.getId(),
                        r.getMessage(),
                        r.getPieceJointeUrl(),
                        r.getEnvoyePar(),
                        r.getDateEnvoi()
                ))
                .toList();
    }


    /* =========================
   🔢 COUNT NON TRAITÉES
   ========================= */
    public Long countNonTraitees() {
        return repository.countByStatut(StatutDemande.PAS_ENCORE_TRAITEE);
    }

    /* =========================
       🗑️ SUPPRIMER UNE DEMANDE
       ========================= */
    @Transactional
    public void delete(Long id) {

        DemandeDevisFormationContinues demande = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));

        if (demande.getReponses() != null) {
            demande.getReponses().forEach(r -> {
                if (r.getPieceJointeUrl() != null) {
                    fileStorageService.deleteQuietly(r.getPieceJointeUrl());
                }
            });
        }

        repository.delete(demande);
    }

    /* =========================
       🔁 MAPPING ENTITY → DTO
       ========================= */
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
                            l.getUniteDuree() != null
                                    ? l.getUniteDuree().name()
                                    : null
                    );

                    return ligne;
                }).toList()
        );

        return dto;
    }
}