package com.school.api.formation.continues.mapper;

import com.school.api.formation.continues.dto.*;
import com.school.api.formation.continues.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CatalogueMapper {

    /* =========================
       LIST → DTO
       ========================= */

    public List<CategorieDTO> toCategorieDTOList(List<CategorieFormationContinues> categories) {
        return categories.stream()
                .map(this::toCategorieDTO)
                .collect(Collectors.toList());
    }

    /* =========================
       CATEGORIE
       ========================= */

    public CategorieDTO toCategorieDTO(CategorieFormationContinues c) {
        CategorieDTO dto = new CategorieDTO();

        dto.setId(c.getId());
        dto.setLibelle(c.getLibelle());

        dto.setSousCategories(
                c.getSousCategories() != null
                        ? c.getSousCategories()
                        .stream()
                        .map(this::toSousCategorieDTO)
                        .collect(Collectors.toList())
                        : List.of()
        );

        return dto;
    }

    /* =========================
       SOUS CATEGORIE
       ========================= */

    public SousCategorieDTO toSousCategorieDTO(SousCategorieFormationContinues sc) {
        SousCategorieDTO dto = new SousCategorieDTO();

        dto.setId(sc.getId());
        dto.setLibelle(sc.getLibelle());

        // 🔥 CORRECTION CRITIQUE
        if (sc.getCategorie() != null) {
            dto.setCategorieId(sc.getCategorie().getId());
        }

        // 🔥 NE PAS CHARGER LES FORMATIONS
        dto.setFormations(List.of());

        return dto;
    }
    /* =========================
       FORMATION (UTILISÉ AILLEURS)
       ========================= */

    public FormationDTO toFormationDTO(FormationContinues f) {
        FormationDTO dto = new FormationDTO();

        dto.setId(f.getId());
        dto.setReference(f.getReference());
        dto.setLibelle(f.getLibelle());
        dto.setDescription(f.getDescription());
        dto.setObjectifs(f.getObjectifs());
        dto.setCompetences(f.getCompetences());
        dto.setPrix(f.getPrix());
        dto.setDuree(f.getDuree());
        dto.setUniteDuree(
                f.getUniteDuree() != null ? f.getUniteDuree().name() : null
        );
        dto.setLogo(f.getLogo());

        return dto;
    }
}