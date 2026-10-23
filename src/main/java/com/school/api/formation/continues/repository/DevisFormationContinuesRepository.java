package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.DevisFormationContinues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DevisFormationContinuesRepository
        extends JpaRepository<DevisFormationContinues, Long> {

    /* Dernier numéro de l'année, pour la numérotation DEV-AAAA-NNNN */
    Optional<DevisFormationContinues> findTopByNumeroStartingWithOrderByNumeroDesc(String prefix);

    boolean existsByEmetteur_Id(Long emetteurId);

    long countByEmetteur_Id(Long emetteurId);
}