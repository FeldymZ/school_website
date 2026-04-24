package com.school.api.formation.preinscription.repository;

import com.school.api.formation.preinscription.entity.PreinscriptionPeriode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PreinscriptionPeriodeRepository extends JpaRepository<PreinscriptionPeriode, Long> {

    /* ================= ACTIVE ================= */
    Optional<PreinscriptionPeriode> findFirstByDateDebutBeforeAndDateFinAfterOrderByDateDebutDesc(
            LocalDateTime now1,
            LocalDateTime now2
    );

    /* ================= ALL WITH RELATIONS ================= */
    @Query("""
        SELECT DISTINCT p FROM PreinscriptionPeriode p
        JOIN FETCH p.session
        JOIN FETCH p.emetteur
        ORDER BY p.dateDebut DESC
    """)
    List<PreinscriptionPeriode> findAllWithRelations();

    /* ================= CHECK FK ================= */
    boolean existsByEmetteur_Id(Long emetteurId);

    long countByEmetteur_Id(Long emetteurId);

    boolean existsBySession_Id(Long sessionId);

    long countBySession_Id(Long sessionId);

    Optional<PreinscriptionPeriode> findFirstByActiveTrueAndDateDebutBeforeAndDateFinAfterOrderByDateDebutDesc(
            LocalDateTime now1,
            LocalDateTime now2
    );
}