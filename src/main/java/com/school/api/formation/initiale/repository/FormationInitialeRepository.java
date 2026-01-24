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

  // Toutes les formations activées
  List<FormationInitiale>
  findByEnabledTrueOrderByDisplayOrderAsc();

  // Par niveau (LICENCE / MASTER)
  List<FormationInitiale>
  findByEnabledTrueAndLevelOrderByDisplayOrderAsc(
    FormationInitialeLevel level
  );

  /* ============================
     🔐 ADMIN
     ============================ */

  List<FormationInitiale>
  findAllByOrderByDisplayOrderAsc();
}
