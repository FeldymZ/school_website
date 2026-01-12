package com.school.api.actualite.repository;

import com.school.api.actualite.entity.ActualiteImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActualiteImageRepository
  extends JpaRepository<ActualiteImage, Long> {

  /* ============================
     🖼️ GALERIE
     ============================ */

  // Images d’une actualité (dans l’ordre)
  List<ActualiteImage>
  findByActualiteIdOrderByDisplayOrderAsc(Long actualiteId);

  // Suppression des images d’une actualité
  void deleteByActualiteId(Long actualiteId);
}
