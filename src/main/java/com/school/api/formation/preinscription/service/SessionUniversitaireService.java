package com.school.api.formation.preinscription.service;

import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.formation.preinscription.entity.SessionUniversitaire;
import com.school.api.formation.preinscription.repository.PreinscriptionPeriodeRepository;
import com.school.api.formation.preinscription.repository.SessionUniversitaireRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionUniversitaireService {

    private final SessionUniversitaireRepository sessionRepo;
    private final PreinscriptionPeriodeRepository periodeRepo;

    /* ================= GET ALL ================= */
    public List<SessionUniversitaire> getAll() {
        return sessionRepo.findAll();
    }

    /* ================= DELETE ================= */
    @Transactional
    public void delete(Long id) {

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
}