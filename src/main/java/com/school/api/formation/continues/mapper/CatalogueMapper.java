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

        if (sc.getCategorie() != null) {
            dto.setCategorieId(sc.getCategorie().getId());
        }

        /* 🔥 AJOUT IMPORTANT : FORMATIONS */
        dto.setFormations(
                sc.getFormations() != null
                        ? sc.getFormations().stream()
                        .filter(FormationContinues::isEnabled) // 🔥 uniquement visibles
                        .map(this::toFormationDTO)
                        .collect(Collectors.toList())
                        : List.of()
        );

        return dto;
    }

    /* =========================
       FORMATION
       ========================= */

    public FormationDTO toFormationDTO(FormationContinues f) {
        FormationDTO dto = new FormationDTO();

        dto.setId(f.getId());
        dto.setReference(f.getReference());
        dto.setSlug(f.getSlug()); // 🔥 AJOUT


        dto.setDescription(f.getDescription());
        dto.setObjectifs(f.getObjectifs());
        dto.setCompetences(f.getCompetences());
        dto.setPrix(f.getPrix());
        dto.setDuree(f.getDuree());

        dto.setUniteDuree(
                f.getUniteDuree() != null ? f.getUniteDuree().name() : null
        );

        dto.setTitre(f.getLibelle());     // ✅
        dto.setCoverUrl(f.getLogo());     // ✅
        dto.setEnabled(f.isEnabled());

        return dto;
    }
}