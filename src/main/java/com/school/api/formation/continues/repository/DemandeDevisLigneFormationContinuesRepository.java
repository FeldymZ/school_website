package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.DemandeDevisLigneFormationContinues;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandeDevisLigneFormationContinuesRepository
        extends JpaRepository<DemandeDevisLigneFormationContinues, Long> {

    /* 🔥 CHECK UTILISATION FORMATION */
    boolean existsByFormationId(Long formationId);
}