package com.school.api.actualite.repository;

import com.school.api.actualite.entity.Actualite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ActualiteRepository extends JpaRepository<Actualite, Long> {

  Optional<Actualite> findBySlug(String slug);

  boolean existsBySlug(String slug);

  // ✅ indispensable pour la mise à jour sans collision
  boolean existsBySlugAndIdNot(String slug, Long id);

  @Query("""
    SELECT a FROM Actualite a
    WHERE a.enabled = true
    ORDER BY a.displayOrder ASC
  """)
  List<Actualite> findPublicVisible();

  List<Actualite> findAllByOrderByDisplayOrderAsc();
}
