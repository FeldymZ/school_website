package com.school.api.formation.preinscription.repository;

import com.school.api.formation.preinscription.entity.PreinscriptionDemande;
import com.school.api.formation.preinscription.entity.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreinscriptionDemandeRepository
        extends JpaRepository<PreinscriptionDemande, Long> {

    List<PreinscriptionDemande> findAllByOrderByCreatedAtDesc();

    List<PreinscriptionDemande> findByStatutOrderByCreatedAtDesc(StatutDemande statut);

    boolean existsByEmailAndPeriodeId(String email, Long periodeId);
}