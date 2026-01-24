
package com.school.api.formation.preinscription.repository;

import com.school.api.formation.preinscription.entity.FormationPreinscription;
import com.school.api.formation.preinscription.entity.enums.StatutPreinscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormationPreinscriptionRepository
  extends JpaRepository<FormationPreinscription, Long> {

  List<FormationPreinscription> findByStatut(StatutPreinscription statut);
}
