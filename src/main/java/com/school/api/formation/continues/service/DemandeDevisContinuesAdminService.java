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
    @Transactional(readOnly = true) // ✅ FIX PRINCIPAL
    public Page<DemandeDevisAdminDTO> getAll(int page, int size) {

        Page<DemandeDevisFormationContinues> result = repository.findAll(
                PageRequest.of(
                        page,
                        size,
                        Sort.by(Sort.Direction.DESC, "dateDemande")
                )
        );

        // ✅ mapping DANS la transaction (plus de lazy error)
        return result.map(this::mapToDTO);
    }

    /* =========================
       🔍 DETAIL
       ========================= */
    @Transactional(readOnly = true) // ✅ sécurité lazy
    public DemandeDevisAdminDTO getById(Long id) {
        return repository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));
    }

    /* =========================
       ✉️ RÉPONDRE
       ========================= */
    @Transactional
    public void repondre(Long demandeId, RepondreDemandeDTO dto) {

        DemandeDevisFormationContinues demande = repository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));

        if (demande.getStatut() == StatutDemande.TRAITEE) {
            throw new RuntimeException("Déjà traitée");
        }

        DemandeDevisReponseContinues reponse = new DemandeDevisReponseContinues();

        reponse.setDemande(demande);
        reponse.setMessage(dto.getMessage());
        reponse.setEnvoyePar("ADMIN");
        reponse.setDateEnvoi(LocalDateTime.now());

        if (dto.getPieceJointe() != null && !dto.getPieceJointe().isEmpty()) {
            String url = fileStorageService
                    .storeDevisContinuesAttachment(dto.getPieceJointe());
            reponse.setPieceJointeUrl(url);
        }

        reponseRepository.save(reponse);

        /* 🔄 MAJ DEMANDE */
        demande.setStatut(StatutDemande.TRAITEE);
        demande.setDateTraitement(LocalDateTime.now());

        repository.save(demande);
    }

    /* =========================
       📩 HISTORIQUE
       ========================= */
    @Transactional(readOnly = true) // ✅ évite lazy bug
    public List<DemandeDevisReponseDTO> getReponses(Long demandeId) {

        return reponseRepository
                .findByDemandeIdOrderByDateEnvoiAsc(demandeId)
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
       🔢 COUNT
       ========================= */
    public Long countNonTraitees() {
        return repository.countByStatut(StatutDemande.PAS_ENCORE_TRAITEE);
    }

    /* =========================
       🗑️ DELETE
       ========================= */
    @Transactional
    public void delete(Long id) {

        DemandeDevisFormationContinues demande = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Introuvable"));

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
       🔁 MAPPING
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

        dto.setStatut(d.getStatut().name());
        dto.setDateTraitement(d.getDateTraitement());

        /* ================= LIGNES ================= */
        dto.setLignes(
                d.getLignes().stream().map(l -> {

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

                }).toList()
        );

        /* ================= REPONSES ================= */
        dto.setReponses(
                d.getReponses() != null
                        ? d.getReponses().stream()
                        .map(r -> new DemandeDevisReponseDTO(
                                r.getId(),
                                r.getMessage(),
                                r.getPieceJointeUrl(),
                                r.getEnvoyePar(),
                                r.getDateEnvoi()
                        ))
                        .toList()
                        : List.of()
        );

        return dto;
    }
}