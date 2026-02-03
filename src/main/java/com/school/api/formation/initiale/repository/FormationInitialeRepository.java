package com.school.api.formation.initiale.repository;

import com.school.api.formation.initiale.entity.FormationInitiale;
import com.school.api.formation.initiale.entity.FormationInitialeLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FormationInitialeRepository
  extends JpaRepository<FormationInitiale, Long> {

  // PUBLIC
  List<FormationInitiale>
  findByEnabledTrueOrderByDisplayOrderAsc();

  List<FormationInitiale>
  findByEnabledTrueAndLevelOrderByDisplayOrderAsc(
    FormationInitialeLevel level
  );

  // ADMIN
  List<FormationInitiale>
  findAllByOrderByDisplayOrderAsc();

  // SLUG
  boolean existsBySlug(String slug);

  Optional<FormationInitiale> findBySlug(String slug);
}
