package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandeDevisFormationContinuesRepository
  extends JpaRepository<DemandeDevisFormationContinues, Long> {

  Page<DemandeDevisFormationContinues> findByStatut(
    StatutDemande statut,
    Pageable pageable
  );

  long countByStatut(StatutDemande statut);
}
