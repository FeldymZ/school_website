package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.SousCategorieFormationContinues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SousCategorieFormationContinuesRepository
        extends JpaRepository<SousCategorieFormationContinues, Long> {

    List<SousCategorieFormationContinues> findByCategorieId(Long categorieId);
}