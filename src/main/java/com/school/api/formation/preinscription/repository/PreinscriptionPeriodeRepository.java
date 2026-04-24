package com.school.api.formation.preinscription.repository;

import com.school.api.formation.preinscription.entity.PreinscriptionPeriode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PreinscriptionPeriodeRepository extends JpaRepository<PreinscriptionPeriode, Long> {

    Optional<PreinscriptionPeriode> findFirstByDateDebutBeforeAndDateFinAfter(
            LocalDateTime now1,
            LocalDateTime now2
    );

    /* 🔥 FIX LAZY */
    @Query("""
        SELECT p FROM PreinscriptionPeriode p
        JOIN FETCH p.session
        JOIN FETCH p.emetteur
    """)
    List<PreinscriptionPeriode> findAllWithRelations();
}