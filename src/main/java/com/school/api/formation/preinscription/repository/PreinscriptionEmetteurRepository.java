package com.school.api.formation.preinscription.repository;

import com.school.api.formation.preinscription.entity.PreinscriptionEmetteur;
import org.springframework.data.jpa.repository.*;
import java.util.List;
import java.util.Optional;

public interface PreinscriptionEmetteurRepository extends JpaRepository<PreinscriptionEmetteur, Long> {

    Optional<PreinscriptionEmetteur> findByActifTrue();

    @Modifying
    @Query("UPDATE PreinscriptionEmetteur e SET e.actif = false")
    void deactivateAll();

    @Query("""
        SELECT e FROM PreinscriptionEmetteur e
        ORDER BY e.actif DESC, e.nom ASC
    """)
    List<PreinscriptionEmetteur> findAllOrdered();

    /* ✅ CORRECTION */
    boolean existsById(Long id);

    long countById(Long id);
}