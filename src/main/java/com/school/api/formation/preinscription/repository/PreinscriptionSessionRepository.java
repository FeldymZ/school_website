package com.school.api.formation.preinscription.repository;

import com.school.api.formation.preinscription.entity.PreinscriptionSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PreinscriptionSessionRepository
        extends JpaRepository<PreinscriptionSession, Long> {

    Optional<PreinscriptionSession> findFirstByDateDebutBeforeAndDateFinAfter(
            LocalDateTime now1,
            LocalDateTime now2
    );

    boolean existsByDateDebutBeforeAndDateFinAfter(
            LocalDateTime end,
            LocalDateTime start
    );
}