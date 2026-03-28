package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.DemandeDevisReponseContinues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandeDevisReponseContinuesRepository
        extends JpaRepository<DemandeDevisReponseContinues, Long> {

    List<DemandeDevisReponseContinues>
    findByDemandeIdOrderByDateEnvoiAsc(Long demandeId);
}