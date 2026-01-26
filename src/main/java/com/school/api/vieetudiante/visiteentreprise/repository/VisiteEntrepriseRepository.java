package com.school.api.vieetudiante.visiteentreprise.repository;

import com.school.api.vieetudiante.visiteentreprise.entity.VisiteEntreprise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisiteEntrepriseRepository
        extends JpaRepository<VisiteEntreprise, Long> {

    List<VisiteEntreprise> findByPublishedTrueOrderByDatePublicationDesc();
}
