package com.school.api.formation.preinscription.repository;

import com.school.api.formation.preinscription.entity.FormationPreinscription;
import com.school.api.formation.preinscription.entity.enums.StatutPreinscription;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormationPreinscriptionRepository
  extends JpaRepository<FormationPreinscription, Long> {

  /**
   * Liste complète des préinscriptions
   * → formation chargée pour DTO admin
   */
  @Override
  @EntityGraph(attributePaths = "formation")
  List<FormationPreinscription> findAll();

  /**
   * Liste filtrée par statut
   * → formation chargée pour DTO admin
   */
  @EntityGraph(attributePaths = "formation")
  List<FormationPreinscription> findByStatut(StatutPreinscription statut);
}
