package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.FormationContinues;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FormationContinuesRepository extends JpaRepository<FormationContinues, Long> {

    /* ================= EXISTANT ================= */

    Page<FormationContinues> findByEnabledTrue(Pageable pageable);

    FormationContinues findByReference(Integer reference);

    Optional<FormationContinues> findBySlug(String slug);

    /* ================= FILTRES ================= */

    Page<FormationContinues> findByEnabledTrueAndSousCategorieIdAndSousCategorieCategorieId(
            Long sousCategorieId,
            Long categorieId,
            Pageable pageable
    );

    Page<FormationContinues> findByEnabledTrueAndSousCategorieCategorieId(
            Long categorieId,
            Pageable pageable
    );

    Page<FormationContinues> findByEnabledTrueAndSousCategorieId(
            Long sousCategorieId,
            Pageable pageable
    );
}