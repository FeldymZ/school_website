package com.school.api.formation.preinscription.service;

import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.common.mail.MailService;
import com.school.api.common.storage.FileStorageService;
import com.school.api.formation.initiale.entity.FormationInitiale;
import com.school.api.formation.initiale.repository.FormationInitialeRepository;
import com.school.api.formation.preinscription.dto.*;
import com.school.api.formation.preinscription.entity.*;
import com.school.api.formation.preinscription.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreinscriptionService {

    private final PreinscriptionDemandeRepository demandeRepo;
    private final PreinscriptionPeriodeRepository periodeRepo;
    private final FormationInitialeRepository formationRepo;
    private final PreinscriptionEmetteurRepository emetteurRepo;
    private final SessionUniversitaireRepository sessionRepo;

    private final MailService mailService;
    private final PreinscriptionJasperService jasperService;
    private final FileStorageService fileStorageService;

    /* =====================================================
       🔹 PUBLIC
       ===================================================== */
    @Transactional
    public PreinscriptionDemandeResponse submit(PreinscriptionDemandeRequest req) {

        PreinscriptionPeriode periode = getActivePeriode();

        if (periode == null) {
            throw new IllegalStateException("Les préinscriptions sont fermées.");
        }

        if (demandeRepo.existsByEmailAndPeriode_Id(req.email(), periode.getId())) {
            throw new IllegalStateException("Demande déjà existante.");
        }

        FormationInitiale formation = formationRepo.findById(req.formationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Formation", "id", req.formationId()
                ));

        PreinscriptionDemande demande = PreinscriptionDemande.builder()
                .civilite(req.civilite())
                .nom(req.nom())
                .prenom(req.prenom())
                .dateNaissance(req.dateNaissance())
                .lieuNaissance(req.lieuNaissance())
                .nationalite(req.nationalite())
                .email(req.email())
                .telephone(req.telephone())
                .whatsapp(req.whatsapp())
                .niveauSouhaite(req.niveauSouhaite())
                .formation(formation)
                .periode(periode)
                .statut(StatutDemande.EN_ATTENTE)
                .build();

        PreinscriptionDemande saved = demandeRepo.save(demande);

        mailService.sendPreinscriptionRecue(
                saved.getEmail(),
                saved.getCivilite().getLabel(),
                saved.getNom(),
                formation.getName(),
                saved.getNiveauSouhaite().getLabel(),
                periode.getSession().getAnnee()
        );

        return toDto(saved);
    }

    /* =====================================================
       🔹 ADMIN — DEMANDES
       ===================================================== */
    public List<PreinscriptionDemandeResponse> getAll() {
        return demandeRepo.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<PreinscriptionDemandeResponse> getByStatut(StatutDemande statut) {
        return demandeRepo.findByStatutOrderByCreatedAtDesc(statut)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<PreinscriptionDemandeResponse> getByFormation(Long formationId) {
        return demandeRepo.findByFormation_IdOrderByCreatedAtDesc(formationId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public PreinscriptionDemandeResponse getById(Long id) {
        return demandeRepo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Demande", "id", id));
    }

    @Transactional
    public PreinscriptionDemandeResponse validate(Long id) {

        PreinscriptionDemande d = demandeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande", "id", id));

        d.setStatut(StatutDemande.VALIDEE);
        d.setValidatedAt(LocalDateTime.now());

        byte[] pdf = jasperService.generatePdf(d);

        String path = fileStorageService.storePreinscriptionPdf(
                pdf,
                "preinscription_" + d.getId() + ".pdf"
        );

        d.setPdfUrl(path);

        return toDto(demandeRepo.save(d));
    }

    @Transactional
    public void reject(Long id) {
        PreinscriptionDemande d = demandeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande", "id", id));

        d.setStatut(StatutDemande.REJETEE);
    }

    /* =====================================================
       🔹 ADMIN — PÉRIODES
       ===================================================== */
    @Transactional
    public void deletePeriode(Long id) {

        PreinscriptionPeriode periode = periodeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PreinscriptionPeriode", "id", id
                ));

        LocalDateTime now = LocalDateTime.now();

        boolean isActive =
                periode.getDateDebut().isBefore(now) &&
                        periode.getDateFin().isAfter(now);

        if (isActive) {
            throw new IllegalStateException("Impossible de supprimer une période active");
        }

        periodeRepo.delete(periode);
    }

    /* =====================================================
       🔹 ADMIN — SESSION
       ===================================================== */
    @Transactional
    public void deleteSession(Long id) {

        SessionUniversitaire session = sessionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SessionUniversitaire", "id", id
                ));

        if (periodeRepo.existsBySession_Id(id)) {
            throw new IllegalStateException(
                    "Impossible de supprimer une session contenant des périodes"
            );
        }

        sessionRepo.delete(session);
    }

    /* =====================================================
       🔹 ADMIN — ÉMETTEURS
       ===================================================== */
    public List<PreinscriptionEmetteur> getAllEmetteurs() {
        return emetteurRepo.findAll();
    }

    @Transactional
    public void createEmetteur(String nom, String fonction, MultipartFile signature) {

        String path = fileStorageService.storeSignature(signature);

        emetteurRepo.save(
                PreinscriptionEmetteur.builder()
                        .nom(nom.trim())
                        .fonction(fonction.trim())
                        .signatureUrl(path)
                        .actif(false)
                        .build()
        );
    }

    @Transactional
    public void activateEmetteur(Long id) {

        emetteurRepo.findAll().forEach(e -> e.setActif(false));

        PreinscriptionEmetteur em = emetteurRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emetteur", "id", id));

        em.setActif(true);
    }

    /* =====================================================
       🔹 ACTIVE SESSION (PUBLIC)
       ===================================================== */
    public SessionPublicResponse getActiveSession() {

        PreinscriptionPeriode p = getActivePeriode();

        if (p == null) {
            return SessionPublicResponse.builder()
                    .ouverte(false)
                    .anneeUniversitaire(null)
                    .dateDebut(null)
                    .dateFin(null)
                    .build();
        }

        return SessionPublicResponse.builder()
                .ouverte(true)
                .anneeUniversitaire(p.getSession().getAnnee())
                .dateDebut(p.getDateDebut())
                .dateFin(p.getDateFin())
                .build();
    }

    public boolean isSessionOuverte() {
        return getActivePeriode() != null;
    }

    /* =====================================================
       🔹 PRIVATE
       ===================================================== */
    private PreinscriptionPeriode getActivePeriode() {
        LocalDateTime now = LocalDateTime.now();
        return periodeRepo
                .findFirstByDateDebutBeforeAndDateFinAfterOrderByDateDebutDesc(now, now)
                .orElse(null);
    }

    private PreinscriptionDemandeResponse toDto(PreinscriptionDemande d) {
        return PreinscriptionDemandeResponse.builder()
                .id(d.getId())
                .civilite(d.getCivilite().getLabel())
                .nom(d.getNom())
                .prenom(d.getPrenom())
                .email(d.getEmail())
                .telephone(d.getTelephone())
                .whatsapp(d.getWhatsapp())
                .niveau(d.getNiveauSouhaite().getLabel())
                .formation(d.getFormation().getName())
                .nationalite(d.getNationalite())
                .anneeUniversitaire(d.getPeriode().getSession().getAnnee())
                .statut(d.getStatut())
                .createdAt(d.getCreatedAt())
                .validatedAt(d.getValidatedAt())
                .pdfUrl(d.getPdfUrl())
                .build();
    }
}