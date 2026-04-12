package com.school.api.formation.continues.service;

import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.formation.continues.dto.CategorieDTO;
import com.school.api.formation.continues.dto.SousCategorieDTO;
import com.school.api.formation.continues.entity.CategorieFormationContinues;
import com.school.api.formation.continues.entity.SousCategorieFormationContinues;
import com.school.api.formation.continues.repository.CategorieFormationContinuesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorieFormationContinuesService {

    private final CategorieFormationContinuesRepository repository;

    /* ================= GET ALL ================= */

    public List<CategorieDTO> getAll() {

        return repository.findAll().stream().map(c -> {

            CategorieDTO dto = new CategorieDTO();
            dto.setId(c.getId());
            dto.setLibelle(c.getLibelle());

            if (c.getSousCategories() != null) {
                dto.setSousCategories(
                        c.getSousCategories().stream().map(sc -> {

                            SousCategorieDTO scDTO = new SousCategorieDTO();
                            scDTO.setId(sc.getId());
                            scDTO.setLibelle(sc.getLibelle());
                            scDTO.setCategorieId(c.getId());

                            return scDTO;

                        }).toList()
                );
            }

            return dto;

        }).toList();
    }

    /* ================= GET BY ID ================= */

    public CategorieDTO getById(Long id) {

        CategorieFormationContinues c = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Categorie", "id", id)
                );

        CategorieDTO dto = new CategorieDTO();
        dto.setId(c.getId());
        dto.setLibelle(c.getLibelle());

        return dto;
    }
}