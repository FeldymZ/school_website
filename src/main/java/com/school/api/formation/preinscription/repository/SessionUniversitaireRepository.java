package com.school.api.formation.preinscription.repository;

import com.school.api.formation.preinscription.entity.SessionUniversitaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionUniversitaireRepository
        extends JpaRepository<SessionUniversitaire, Long> {

    Optional<SessionUniversitaire> findByAnnee(String annee);
}