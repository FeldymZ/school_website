package com.school.api.formation.continues.mapper;

import com.school.api.formation.continues.dto.FormationDTO;
import com.school.api.formation.continues.dto.SousCategorieDTO;
import com.school.api.formation.continues.entity.FormationContinues;
import org.springframework.stereotype.Component;

@Component
public class FormationMapper {

    public FormationDTO toDTO(FormationContinues f) {

        FormationDTO dto = new FormationDTO();

        dto.setId(f.getId());
        dto.setReference(f.getReference());
        dto.setSlug(f.getSlug());

        /* 🔥 ALIGNEMENT FRONT */
        dto.setTitre(f.getLibelle());

        dto.setDescription(f.getDescription());
        dto.setObjectifs(f.getObjectifs());
        dto.setCompetences(f.getCompetences());

        dto.setPrix(f.getPrix());
        dto.setDuree(f.getDuree());

        dto.setUniteDuree(
                f.getUniteDuree() != null ? f.getUniteDuree().name() : null
        );

        /* 🔥 ALIGNEMENT FRONT */
        dto.setCoverUrl(f.getLogo());

        dto.setEnabled(f.isEnabled());

        if (f.getSousCategorie() != null) {
            SousCategorieDTO sc = new SousCategorieDTO();
            sc.setId(f.getSousCategorie().getId());
            sc.setLibelle(f.getSousCategorie().getLibelle());

            if (f.getSousCategorie().getCategorie() != null) {
                sc.setCategorieId(f.getSousCategorie().getCategorie().getId());
            }

            dto.setSousCategorie(sc);
        }

        return dto;
    }
}