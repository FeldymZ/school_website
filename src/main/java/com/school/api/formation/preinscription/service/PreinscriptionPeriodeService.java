package com.school.api.formation.preinscription.service;

import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.formation.preinscription.dto.*;
import com.school.api.formation.preinscription.entity.*;
import com.school.api.formation.preinscription.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreinscriptionPeriodeService {

    private final PreinscriptionPeriodeRepository periodeRepo;
    private final SessionUniversitaireRepository sessionRepo;
    private final PreinscriptionEmetteurRepository emetteurRepo;

    /* ================= TIME ================= */
    private LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of("Africa/Libreville"));
    }

    /* ================= SESSION ================= */
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

    public List<SessionUniversitaire> getAllSessions() {
        return sessionRepo.findAll();
    }

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

    /* ================= PERIODE ================= */
    @Transactional
    public void createPeriode(PeriodeRequest req) {

        if (req.dateFin().isBefore(req.dateDebut())) {
            throw new IllegalArgumentException("Dates invalides");
        }

        SessionUniversitaire session = sessionRepo.findById(req.sessionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SessionUniversitaire", "id", req.sessionId()
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
                        .active(true) // ✅ IMPORTANT
                        .build()
        );
    }

    public List<PreinscriptionPeriode> getAll() {
        return periodeRepo.findAllWithRelations();
    }

    /* ================= DESACTIVER ================= */
    @Transactional
    public void deactivatePeriode(Long id) {

        PreinscriptionPeriode periode = periodeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PreinscriptionPeriode", "id", id
                ));

        periode.setActive(false);
    }

    /* ================= DELETE ================= */
    @Transactional
    public void deletePeriode(Long id) {

        PreinscriptionPeriode periode = periodeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PreinscriptionPeriode", "id", id
                ));

        /* ✅ FIX : basé sur active */
        if (periode.isActive()) {
            throw new IllegalStateException(
                    "Désactivez d'abord la période avant suppression"
            );
        }

        periodeRepo.delete(periode);
    }

    /* ================= ACTIVE ================= */
    public PreinscriptionPeriode getActivePeriode() {

        LocalDateTime now = now();

        /* ✅ FIX : utilise active */
        return periodeRepo
                .findFirstByActiveTrueAndDateDebutBeforeAndDateFinAfterOrderByDateDebutDesc(now, now)
                .orElse(null);
    }

    public boolean isPeriodeActive() {
        return getActivePeriode() != null;
    }

    public PeriodePublicResponse getPublicInfo() {

        PreinscriptionPeriode p = getActivePeriode();

        if (p == null) {
            return PeriodePublicResponse.builder()
                    .ouverte(false)
                    .anneeUniversitaire(null)
                    .build();
        }

        return PeriodePublicResponse.builder()
                .ouverte(true)
                .anneeUniversitaire(p.getSession().getAnnee())
                .build();
    }

    /* ================= VERIFIER SI OUVERT ================= */
    public boolean isOuverte(PreinscriptionPeriode periode) {

        LocalDateTime now = now();

        /* ✅ FIX CRUCIAL */
        return periode.isActive() &&
                now.isAfter(periode.getDateDebut()) &&
                now.isBefore(periode.getDateFin());
    }

    /* ================= UPDATE ================= */
    @Transactional
    public void updateSession(Long id, SessionUniversitaireRequest req) {

        SessionUniversitaire session = sessionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SessionUniversitaire", "id", id
                ));

        session.setAnnee(req.annee());
    }

    @Transactional
    public void updatePeriode(Long id, PeriodeRequest req) {

        if (req.dateFin().isBefore(req.dateDebut())) {
            throw new IllegalArgumentException("Dates invalides");
        }

        PreinscriptionPeriode periode = periodeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PreinscriptionPeriode", "id", id
                ));

        SessionUniversitaire session = sessionRepo.findById(req.sessionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SessionUniversitaire", "id", req.sessionId()
                ));

        PreinscriptionEmetteur emetteur = emetteurRepo.findById(req.emetteurId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Emetteur", "id", req.emetteurId()
                ));

        periode.setSession(session);
        periode.setEmetteur(emetteur);
        periode.setDateDebut(req.dateDebut());
        periode.setDateFin(req.dateFin());
    }
}