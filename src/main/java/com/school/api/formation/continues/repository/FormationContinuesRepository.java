package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.FormationContinues;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FormationContinuesRepository
  extends JpaRepository<FormationContinues, Long> {

  Optional<FormationContinues> findBySlug(String slug);

  Page<FormationContinues> findByEnabledTrue(Pageable pageable);
}
