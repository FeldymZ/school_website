package com.school.api.formation.preinscription.service;

import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.formation.preinscription.dto.*;
import com.school.api.formation.preinscription.entity.*;
import com.school.api.formation.preinscription.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PreinscriptionPeriodeService {

    private final PreinscriptionPeriodeRepository periodeRepo;
    private final SessionUniversitaireRepository sessionRepo;
    private final PreinscriptionEmetteurRepository emetteurRepo;

    /* ================= CREATE SESSION ================= */
    public void createSession(SessionUniversitaireRequest req) {

        if (sessionRepo.findByAnnee(req.annee()).isPresent()) {
            throw new IllegalStateException("Session déjà existante");
        }

        sessionRepo.save(
                SessionUniversitaire.builder()
                        .annee(req.annee().trim())
                        .build()
        );
    }

    /* ================= CREATE PERIODE ================= */
    @Transactional
    public void createPeriode(PeriodeRequest req) {

        if (req.dateFin().isBefore(req.dateDebut())) {
            throw new IllegalArgumentException("Date fin invalide");
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

    /* ================= ACTIVE ================= */
    @Transactional(readOnly = true)
    public PreinscriptionPeriode getActivePeriode() {

        LocalDateTime now = LocalDateTime.now();

        return periodeRepo
                .findFirstByDateDebutBeforeAndDateFinAfter(now, now)
                .orElse(null);
    }

    /* ================= PUBLIC ================= */
    @Transactional(readOnly = true)
    public SessionPublicResponse getPublicSession() {

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

    public boolean isPeriodeActive() {
        return getActivePeriode() != null;
    }
}