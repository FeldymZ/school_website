package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.DemandeDevisFormationContinues;
import com.school.api.formation.continues.entity.StatutDemande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandeDevisFormationContinuesRepository
        extends JpaRepository<DemandeDevisFormationContinues, Long> {

    Page<DemandeDevisFormationContinues> findByStatut(StatutDemande statut, Pageable pageable);

    Long countByStatut(StatutDemande statut);
}