package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.CategorieFormationContinues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategorieFormationContinuesRepository
        extends JpaRepository<CategorieFormationContinues, Long> {

    @Query("""
        SELECT DISTINCT c FROM CategorieFormationContinues c
        LEFT JOIN FETCH c.sousCategories sc
        LEFT JOIN FETCH sc.formations f
    """)
    List<CategorieFormationContinues> findAllWithSousCategoriesAndFormations();
}