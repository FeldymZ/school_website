package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.FormationContinues;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormationContinuesRepository
        extends JpaRepository<FormationContinues, Long> {

    Page<FormationContinues> findByEnabledTrue(Pageable pageable);

    /* 🔥 FILTRES */
    Page<FormationContinues> findBySousCategorieId(Long sousCategorieId, Pageable pageable);

    Page<FormationContinues> findBySousCategorieCategorieId(Long categorieId, Pageable pageable);

    /* 🔥 SEARCH */
    FormationContinues findByReference(Integer reference);
}