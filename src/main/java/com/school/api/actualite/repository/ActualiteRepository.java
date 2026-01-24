package com.school.api.actualite.repository;

import com.school.api.actualite.entity.Actualite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActualiteRepository extends JpaRepository<Actualite, Long> {

  /* ============================
     🌍 PUBLIC
     ============================ */

  /**
   * Une actualité est visible publiquement SI ET SEULEMENT SI :
   * - enabled = true
   * - elle possède au moins une image de galerie
   */
  @Query("""
    SELECT a FROM Actualite a
    WHERE a.enabled = true
      AND EXISTS (
        SELECT i FROM ActualiteImage i
        WHERE i.actualite.id = a.id
      )
    ORDER BY a.displayOrder ASC
  """)
  List<Actualite> findPublicVisible();

  /* ============================
     🔐 ADMIN
     ============================ */

  // Liste complète admin
  List<Actualite> findAllByOrderByDisplayOrderAsc();
}
