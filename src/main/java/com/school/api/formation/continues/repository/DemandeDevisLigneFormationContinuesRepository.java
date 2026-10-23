package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.DemandeDevisLigneFormationContinues;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandeDevisLigneFormationContinuesRepository
        extends JpaRepository<DemandeDevisLigneFormationContinues, Long> {

    /* Vérifie si une formation est utilisée dans une demande */
    boolean existsByFormationId(Long formationId);
}