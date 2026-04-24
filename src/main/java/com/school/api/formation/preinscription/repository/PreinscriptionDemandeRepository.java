package com.school.api.formation.preinscription.repository;

import com.school.api.formation.preinscription.entity.PreinscriptionDemande;
import com.school.api.formation.preinscription.entity.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PreinscriptionDemandeRepository
        extends JpaRepository<PreinscriptionDemande, Long> {

    /* =========================
       🔥 FETCH COMPLET
    ========================= */
    @Query("""
        SELECT d FROM PreinscriptionDemande d
        JOIN FETCH d.formation f
        JOIN FETCH d.periode p
        JOIN FETCH p.session s
        ORDER BY d.createdAt DESC
    """)
    List<PreinscriptionDemande> findAllWithRelations();

    @Query("""
        SELECT d FROM PreinscriptionDemande d
        JOIN FETCH d.formation f
        JOIN FETCH d.periode p
        JOIN FETCH p.session s
        WHERE d.statut = :statut
        ORDER BY d.createdAt DESC
    """)
    List<PreinscriptionDemande> findByStatutWithRelations(StatutDemande statut);

    @Query("""
        SELECT d FROM PreinscriptionDemande d
        JOIN FETCH d.formation f
        JOIN FETCH d.periode p
        JOIN FETCH p.session s
        WHERE f.id = :formationId
        ORDER BY d.createdAt DESC
    """)
    List<PreinscriptionDemande> findByFormationWithRelations(Long formationId);

    boolean existsByEmailAndPeriode_Id(String email, Long periodeId);
}