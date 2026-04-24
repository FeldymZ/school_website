package com.school.api.formation.preinscription.service;

import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.formation.preinscription.dto.*;
import com.school.api.formation.preinscription.entity.*;
import com.school.api.formation.preinscription.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PreinscriptionPeriodeService {

    private final PreinscriptionPeriodeRepository periodeRepo;
    private final SessionUniversitaireRepository sessionRepo;
    private final PreinscriptionEmetteurRepository emetteurRepo;

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
                        .build()
        );
    }

    /* ================= ACTIVE ================= */
    public PreinscriptionPeriode getActivePeriode() {
        LocalDateTime now = LocalDateTime.now();

        return periodeRepo
                .findFirstByDateDebutBeforeAndDateFinAfter(now, now)
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
}