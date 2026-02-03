package com.school.api.actualite.repository;

import com.school.api.actualite.entity.Actualite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ActualiteRepository extends JpaRepository<Actualite, Long> {

  Optional<Actualite> findBySlug(String slug);

  boolean existsBySlug(String slug);

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

  List<Actualite> findAllByOrderByDisplayOrderAsc();
}
