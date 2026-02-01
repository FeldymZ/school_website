package com.school.api.formation.initiale.repository;

import com.school.api.formation.initiale.entity.FormationInitialeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FormationInitialeImageRepository
  extends JpaRepository<FormationInitialeImage, Long> {

  List<FormationInitialeImage>
  findByFormationIdOrderByDisplayOrderAsc(Long formationId);

  @Modifying
  @Transactional
  void deleteByFormationId(Long formationId);
}
