package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.InscriptionSessionFormation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface InscriptionSessionFormationRepository extends JpaRepository<InscriptionSessionFormation, Long> {

    long countBySession_IdAndStatutIn(Long sessionId, Collection<String> statuts);

    long countBySession_Id(Long sessionId);

    long countByStatut(String statut);

    boolean existsBySession_IdAndEmailIgnoreCaseAndStatutIn(Long sessionId, String email, Collection<String> statuts);

    /* ===== Listes (session + formation chargées en une requête) ===== */

    @EntityGraph(attributePaths = {"session", "session.formation"})
    Page<InscriptionSessionFormation> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = {"session", "session.formation"})
    Page<InscriptionSessionFormation> findByStatut(String statut, Pageable pageable);

    @EntityGraph(attributePaths = {"session", "session.formation"})
    Page<InscriptionSessionFormation> findBySession_Id(Long sessionId, Pageable pageable);

    @EntityGraph(attributePaths = {"session", "session.formation"})
    Page<InscriptionSessionFormation> findByStatutAndSession_Id(String statut, Long sessionId, Pageable pageable);

    /** Export : toutes les inscriptions d'une session */
    @EntityGraph(attributePaths = {"session", "session.formation"})
    List<InscriptionSessionFormation> findBySession_IdOrderByNomAscPrenomAsc(Long sessionId);
}