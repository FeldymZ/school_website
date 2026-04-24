package com.school.api.formation.preinscription.repository;

import com.school.api.formation.preinscription.entity.PreinscriptionDemande;
import com.school.api.formation.preinscription.entity.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreinscriptionDemandeRepository
        extends JpaRepository<PreinscriptionDemande, Long> {

    List<PreinscriptionDemande>
    findAllByOrderByCreatedAtDesc();

    List<PreinscriptionDemande>
    findByStatutOrderByCreatedAtDesc(StatutDemande statut);

    List<PreinscriptionDemande>
    findByFormation_IdOrderByCreatedAtDesc(Long formationId);

    boolean existsByEmailAndPeriode_Id(String email, Long periodeId);
}