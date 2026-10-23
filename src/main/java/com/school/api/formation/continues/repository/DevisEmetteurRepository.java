package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.DevisEmetteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DevisEmetteurRepository extends JpaRepository<DevisEmetteur, Long> {

    @Query("SELECT e FROM DevisEmetteur e ORDER BY e.actif DESC, e.nom ASC")
    List<DevisEmetteur> findAllOrdered();

    Optional<DevisEmetteur> findByActifTrue();

    @Modifying
    @Query("UPDATE DevisEmetteur e SET e.actif = false WHERE e.actif = true")
    void deactivateAll();
}