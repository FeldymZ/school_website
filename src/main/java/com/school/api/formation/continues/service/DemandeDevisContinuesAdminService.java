package com.school.api.formation.continues.service;

import com.school.api.common.mail.MailService;
import com.school.api.common.storage.ByteArrayMultipartFile;
import com.school.api.common.storage.FileStorageService;
import com.school.api.formation.continues.dto.DemandeDevisAdminDTO;
import com.school.api.formation.continues.dto.DemandeDevisReponseDTO;
import com.school.api.formation.continues.dto.RepondreDemandeDTO;
import com.school.api.formation.continues.entity.*;
import com.school.api.formation.continues.mapper.DemandeDevisMapper;
import com.school.api.formation.continues.repository.DemandeDevisFormationContinuesRepository;
import com.school.api.formation.continues.repository.DemandeDevisReponseContinuesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DemandeDevisContinuesAdminService {

    private final DemandeDevisFormationContinuesRepository repository;
    private final DemandeDevisReponseContinuesRepository reponseRepository;
    private final DevisContinuesService devisService;
    private final DemandeDevisMapper mapper;
    private final FileStorageService fileStorageService;
    private final MailService mailService;

    /* =========================
       📄 LISTE PAGINÉE (+ filtre statut)
       ========================= */
    @Transactional(readOnly = true)
    public Page<DemandeDevisAdminDTO> getAll(int page, int size, StatutDemande statut) {

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "dateDemande")
        );

        Page<DemandeDevisFormationContinues> result = (statut == null)
                ? repository.findAll(pageable)
                : repository.findByStatut(statut, pageable);

        return result.map(mapper::toDTO);
    }

    /* =========================
       📊 STATISTIQUES
       ========================= */
    @Transactional(readOnly = true)
    public Map<String, Long> stats() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("total", repository.count());
        stats.put("nonTraitees", repository.countByStatut(StatutDemande.PAS_ENCORE_TRAITEE));
        stats.put("enCours", repository.countByStatut(StatutDemande.EN_COURS));
        stats.put("fermees", repository.countByStatut(StatutDemande.FERMEE));
        return stats;
    }

    public Long countNonTraitees() {
        return repository.countByStatut(StatutDemande.PAS_ENCORE_TRAITEE);
    }

    /* =========================
       🔍 DÉTAIL
       ========================= */
    @Transactional(readOnly = true)
    public DemandeDevisAdminDTO getById(Long id) {
        return mapper.toDTO(trouver(id));
    }

    /* =========================
       ✉️ RÉPONDRE (message libre + PJ optionnelle)
       ========================= */
    @Transactional
    public void repondre(Long demandeId, RepondreDemandeDTO dto) {

        DemandeDevisFormationContinues demande = trouver(demandeId);

        if (demande.getStatut() == StatutDemande.FERMEE) {
            throw new RuntimeException("Demande clôturée");
        }
        if (dto.getMessage() == null || dto.getMessage().isBlank()) {
            throw new RuntimeException("Le message est obligatoire");
        }

        DemandeDevisReponseContinues reponse = new DemandeDevisReponseContinues();
        reponse.setDemande(demande);
        reponse.setMessage(dto.getMessage());
        reponse.setEnvoyePar("ADMIN");
        reponse.setDateEnvoi(LocalDateTime.now());

        if (dto.getPieceJointe() != null && !dto.getPieceJointe().isEmpty()) {
            reponse.setPieceJointeUrl(
                    fileStorageService.storeDevisContinuesAttachment(dto.getPieceJointe()));
        }

        reponseRepository.save(reponse);

        if (demande.getStatut() == StatutDemande.PAS_ENCORE_TRAITEE) {
            demande.setStatut(StatutDemande.EN_COURS);
        }
        demande.setDateTraitement(LocalDateTime.now());
        repository.save(demande);

        mailService.sendDevisResponse(
                demande.getEmail(),
                demande.getNomClient(),
                dto.getMessage(),
                reponse.getPieceJointeUrl()
        );
    }

    /* =========================
       📨 ENVOYER LE DEVIS (PDF en pièce jointe)
       ========================= */
    @Transactional
    public void envoyerDevis(Long demandeId, String message) {

        DemandeDevisFormationContinues demande = trouver(demandeId);
        DevisFormationContinues devis = demande.getDevis();

        if (devis == null) {
            throw new RuntimeException("Aucun devis validé pour cette demande");
        }
        if (demande.getStatut() == StatutDemande.FERMEE) {
            throw new RuntimeException("Demande clôturée");
        }

        DevisContinuesService.DevisPdf pdf = devisService.pdf(demandeId);

        RepondreDemandeDTO dto = new RepondreDemandeDTO();
        dto.setMessage(message != null && !message.isBlank() ? message : messageParDefaut(demande, devis));
        dto.setPieceJointe(new ByteArrayMultipartFile(
                "pieceJointe", pdf.nomFichier(), "application/pdf", pdf.contenu()));

        repondre(demandeId, dto);

        devis.setDateEnvoi(LocalDateTime.now());
    }

    /* =========================
       📩 HISTORIQUE
       ========================= */
    @Transactional(readOnly = true)
    public List<DemandeDevisReponseDTO> getReponses(Long demandeId) {
        return reponseRepository
                .findByDemandeIdOrderByDateEnvoiAsc(demandeId)
                .stream()
                .map(mapper::toReponseDTO)
                .toList();
    }

    /* =========================
       🔒 CLÔTURER
       ========================= */
    @Transactional
    public void cloturer(Long id) {
        DemandeDevisFormationContinues demande = trouver(id);
        demande.setStatut(StatutDemande.FERMEE);
        demande.setDateTraitement(LocalDateTime.now());
        repository.save(demande);
    }

    /* =========================
       🗑️ SUPPRIMER (devis, lignes et réponses en cascade)
       ========================= */
    @Transactional
    public void delete(Long id) {

        DemandeDevisFormationContinues demande = trouver(id);

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
       HELPERS
       ========================= */
    private DemandeDevisFormationContinues trouver(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));
    }

    private String messageParDefaut(DemandeDevisFormationContinues d, DevisFormationContinues devis) {
        return "Bonjour " + d.getNomClient() + ",\n\n"
                + "Suite à votre demande, veuillez trouver ci-joint notre devis n° " + devis.getNumero()
                + ", valable jusqu'au " + devis.getDateValidite().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".\n\n"
                + "Pour confirmer, merci de nous retourner le devis signé avec la mention « Bon pour accord ».\n\n"
                + "Cordialement,\nESIITECH Gabon";
    }
}