package com.school.api.formation.continues.service;

import com.school.api.formation.continues.dto.*;
import com.school.api.formation.continues.entity.*;
import com.school.api.formation.continues.repository.CategorieFormationContinuesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogueService {

    private final CategorieFormationContinuesRepository repository;

    public List<CategorieDTO> getCatalogue() {

        List<CategorieFormationContinues> categories = repository.findAll();

        return categories.stream().map(c -> {

            CategorieDTO catDTO = new CategorieDTO();
            catDTO.setId(c.getId());
            catDTO.setLibelle(c.getLibelle());

            if (c.getSousCategories() != null) {
                catDTO.setSousCategories(
                        c.getSousCategories().stream().map(sc -> {

                            SousCategorieDTO scDTO = new SousCategorieDTO();
                            scDTO.setId(sc.getId());
                            scDTO.setLibelle(sc.getLibelle());

                            if (sc.getFormations() != null) {
                                scDTO.setFormations(
                                        sc.getFormations().stream()
                                                .filter(FormationContinues::isEnabled)
                                                .map(f -> {

                                                    FormationDTO fDTO = new FormationDTO();
                                                    fDTO.setId(f.getId());
                                                    fDTO.setReference(f.getReference());
                                                    fDTO.setSlug(f.getSlug());

                                                    fDTO.setLibelle(f.getLibelle());
                                                    fDTO.setDescription(f.getDescription());
                                                    fDTO.setPrix(f.getPrix());
                                                    fDTO.setDuree(f.getDuree());

                                                    fDTO.setUniteDuree(
                                                            f.getUniteDuree() != null
                                                                    ? f.getUniteDuree().name()
                                                                    : null
                                                    );

                                                    fDTO.setLogo(f.getLogo());
                                                    fDTO.setEnabled(f.isEnabled());

                                                    return fDTO;
                                                }).toList()
                                );
                            }

                            return scDTO;

                        }).toList()
                );
            }

            return catDTO;

        }).toList();
    }
}