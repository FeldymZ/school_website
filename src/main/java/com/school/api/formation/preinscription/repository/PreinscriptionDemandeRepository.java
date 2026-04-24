package com.school.api.formation.preinscription.repository;

import com.school.api.formation.preinscription.entity.PreinscriptionDemande;
import com.school.api.formation.preinscription.entity.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PreinscriptionDemandeRepository
        extends JpaRepository<PreinscriptionDemande, Long> {

    /* ================= FETCH ALL ================= */
    @Query("""
        SELECT d FROM PreinscriptionDemande d
        JOIN FETCH d.formation f
        JOIN FETCH d.periode p
        JOIN FETCH p.session s
        ORDER BY d.createdAt DESC
    """)
    List<PreinscriptionDemande> findAllWithRelations();

    /* ================= FETCH STATUT ================= */
    @Query("""
        SELECT d FROM PreinscriptionDemande d
        JOIN FETCH d.formation f
        JOIN FETCH d.periode p
        JOIN FETCH p.session s
        WHERE d.statut = :statut
        ORDER BY d.createdAt DESC
    """)
    List<PreinscriptionDemande> findByStatutWithRelations(StatutDemande statut);

    /* ================= FETCH FORMATION ================= */
    @Query("""
        SELECT d FROM PreinscriptionDemande d
        JOIN FETCH d.formation f
        JOIN FETCH d.periode p
        JOIN FETCH p.session s
        WHERE f.id = :formationId
        ORDER BY d.createdAt DESC
    """)
    List<PreinscriptionDemande> findByFormationWithRelations(Long formationId);

    /* 🔥🔥🔥 FIX CRITIQUE */
    @Query("""
        SELECT d FROM PreinscriptionDemande d
        JOIN FETCH d.formation f
        JOIN FETCH d.periode p
        JOIN FETCH p.session s
        WHERE d.id = :id
    """)
    Optional<PreinscriptionDemande> findByIdWithRelations(Long id);

    boolean existsByEmailAndPeriode_Id(String email, Long periodeId);
}