package com.school.api.formation.initiale.repository;

import com.school.api.formation.initiale.entity.FormationInitialeImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;




public interface FormationInitialeImageRepository
  extends JpaRepository<FormationInitialeImage, Long> {

  /* ============================
     🌍 / 🔐
     ============================ */

  // Récupérer les images secondaires d’une formation
  List<FormationInitialeImage>
  findByFormationIdOrderByDisplayOrderAsc(Long formationId);

  // Suppression en masse (utile si on supprime une formation)
  void deleteByFormationId(Long formationId);
}
