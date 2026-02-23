package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.DemandeDevisReponseContinues;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandeDevisReponseContinuesRepository
  extends JpaRepository<DemandeDevisReponseContinues, Long> {
}
