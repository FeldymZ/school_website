package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.CategorieFormationContinues;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategorieFormationContinuesRepository
        extends JpaRepository<CategorieFormationContinues, Long> {

    /* ================= FETCH COMPLET ================= */

    @Query("""
        SELECT DISTINCT c
        FROM CategorieFormationContinues c
        LEFT JOIN FETCH c.sousCategories sc
    """)
    List<CategorieFormationContinues> findAllWithSousCategories();
}