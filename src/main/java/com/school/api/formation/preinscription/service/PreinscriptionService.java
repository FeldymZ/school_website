package com.school.api.formation.preinscription.service;

import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.common.mail.MailService;
import com.school.api.common.storage.FileStorageService;
import com.school.api.formation.initiale.entity.FormationInitiale;
import com.school.api.formation.initiale.repository.FormationInitialeRepository;
import com.school.api.formation.preinscription.dto.*;
import com.school.api.formation.preinscription.entity.*;
import com.school.api.formation.preinscription.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreinscriptionService {

    private final PreinscriptionDemandeRepository demandeRepo;
    private final PreinscriptionPeriodeRepository periodeRepo;
    private final SessionUniversitaireRepository sessionRepo;
    private final PreinscriptionEmetteurRepository emetteurRepo;
    private final FormationInitialeRepository formationRepo;
    private final MailService mailService;
    private final PreinscriptionJasperService jasperService;
    private final FileStorageService fileStorageService;

    /* =========================
       🔹 PUBLIC
       ========================= */
    @Transactional
    public PreinscriptionDemandeResponse submit(PreinscriptionDemandeRequest req) {

        PreinscriptionPeriode periode = getActivePeriode();

        if (periode == null) {
            throw new IllegalStateException("Les préinscriptions sont fermées.");
        }

        if (demandeRepo.existsByEmailAndPeriodeId(req.email(), periode.getId())) {
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

    /* =========================
       🔹 SESSION UNIVERSITAIRE
       ========================= */
    @Transactional
    public void createSession(SessionUniversitaireRequest req) {

        if (sessionRepo.findByAnnee(req.annee()).isPresent()) {
            throw new IllegalStateException("Session déjà existante");
        }

        sessionRepo.save(
                SessionUniversitaire.builder()
                        .annee(req.annee())
                        .build()
        );
    }

    public List<SessionUniversitaireResponse> getAllSessions() {
        return sessionRepo.findAll()
                .stream()
                .map(s -> new SessionUniversitaireResponse(
                        s.getId(),
                        s.getAnnee()
                ))
                .toList();
    }

    /* =========================
       🔹 PERIODE
       ========================= */
    @Transactional
    public void createPeriode(PeriodeRequest req) {

        if (req.dateFin().isBefore(req.dateDebut())) {
            throw new IllegalArgumentException("Dates invalides");
        }

        SessionUniversitaire session = sessionRepo.findById(req.sessionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session", "id", req.sessionId()
                ));

        PreinscriptionEmetteur emetteur = emetteurRepo.findById(req.emetteurId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Emetteur", "id", req.emetteurId()
                ));

        periodeRepo.save(
                PreinscriptionPeriode.builder()
                        .session(session)
                        .emetteur(emetteur)
                        .dateDebut(req.dateDebut())
                        .dateFin(req.dateFin())
                        .build()
        );
    }

    public List<PreinscriptionPeriode> getAllPeriodes() {
        return periodeRepo.findAll();
    }

    /* =========================
       🔹 ACTIVE
       ========================= */
    public SessionPublicResponse getActiveSession() {

        PreinscriptionPeriode p = getActivePeriode();

        if (p == null) {
            return SessionPublicResponse.builder()
                    .ouverte(false)
                    .build();
        }

        return SessionPublicResponse.builder()
                .ouverte(true)
                .anneeUniversitaire(p.getSession().getAnnee())
                .dateDebut(p.getDateDebut())
                .dateFin(p.getDateFin())
                .build();
    }

    private PreinscriptionPeriode getActivePeriode() {
        LocalDateTime now = LocalDateTime.now();
        return periodeRepo
                .findFirstByDateDebutBeforeAndDateFinAfter(now, now)
                .orElse(null);
    }

    /* =========================
       🔹 DTO
       ========================= */
    private PreinscriptionDemandeResponse toDto(PreinscriptionDemande d) {
        return PreinscriptionDemandeResponse.builder()
                .id(d.getId())
                .nom(d.getNom())
                .prenom(d.getPrenom())
                .email(d.getEmail())
                .formation(d.getFormation().getName())
                .anneeUniversitaire(d.getPeriode().getSession().getAnnee())
                .statut(d.getStatut())
                .createdAt(d.getCreatedAt())
                .validatedAt(d.getValidatedAt())
                .pdfUrl(d.getPdfUrl())
                .build();
    }
}