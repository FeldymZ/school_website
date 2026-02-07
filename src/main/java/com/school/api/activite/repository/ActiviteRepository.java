package com.school.api.activite.repository;

import com.school.api.activite.entity.Activite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActiviteRepository extends JpaRepository<Activite, Long> {

  Optional<Activite> findBySlug(String slug);

  boolean existsBySlug(String slug);
}
