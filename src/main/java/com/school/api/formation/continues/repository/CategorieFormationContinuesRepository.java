package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.CategorieFormationContinues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorieFormationContinuesRepository
        extends JpaRepository<CategorieFormationContinues, Long> {

    // 🔥 PLUS DE FETCH ICI
}