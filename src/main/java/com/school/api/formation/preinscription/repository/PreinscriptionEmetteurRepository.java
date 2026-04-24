package com.school.api.formation.preinscription.repository;

import com.school.api.formation.preinscription.entity.PreinscriptionEmetteur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreinscriptionEmetteurRepository
        extends JpaRepository<PreinscriptionEmetteur, Long> {

    Optional<PreinscriptionEmetteur> findByActifTrue();
}