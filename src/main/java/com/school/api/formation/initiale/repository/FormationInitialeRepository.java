package com.school.api.formation.initiale.repository;

import com.school.api.formation.initiale.entity.FormationInitiale;
import com.school.api.formation.initiale.entity.FormationInitialeLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormationInitialeRepository
  extends JpaRepository<FormationInitiale, Long> {

  /* ============================
     🌍 PUBLIC
     ============================ */

  // Liste publique par niveau (LICENCE / MASTER)
  List<FormationInitiale>
  findByEnabledTrueAndLevelOrderByDisplayOrderAsc(
    FormationInitialeLevel level
  );

  /* ============================
     🔐 ADMIN
     ============================ */

  // Liste complète admin
  List<FormationInitiale> findAllByOrderByDisplayOrderAsc();
}
