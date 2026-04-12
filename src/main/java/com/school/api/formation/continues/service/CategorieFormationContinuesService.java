package com.school.api.formation.continues.service;

import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.formation.continues.dto.CategorieDTO;
import com.school.api.formation.continues.dto.SousCategorieDTO;
import com.school.api.formation.continues.entity.CategorieFormationContinues;
import com.school.api.formation.continues.repository.CategorieFormationContinuesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorieFormationContinuesService {

    private final CategorieFormationContinuesRepository repository;

    /* ================= CREATE ================= */

    public CategorieDTO create(String libelle) {

        CategorieFormationContinues c = new CategorieFormationContinues();
        c.setLibelle(libelle);

        return toDTO(repository.save(c));
    }

    /* ================= UPDATE ================= */

    public CategorieDTO update(Long id, String libelle) {

        CategorieFormationContinues c = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Categorie", "id", id)
                );

        c.setLibelle(libelle);

        return toDTO(repository.save(c));
    }

    /* ================= DELETE ================= */

    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Categorie", "id", id);
        }

        repository.deleteById(id);
    }

    /* ================= GET ALL ================= */

    @Transactional(readOnly = true) // 🔥 FIX IMPORTANT
    public List<CategorieDTO> getAll() {

        return repository.findAll().stream().map(this::toDTOWithSousCategories).toList();
    }

    /* ================= GET BY ID ================= */

    public CategorieDTO getById(Long id) {

        CategorieFormationContinues c = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Categorie", "id", id)
                );

        return toDTO(c);
    }

    /* ================= MAPPING ================= */

    private CategorieDTO toDTO(CategorieFormationContinues c) {

        CategorieDTO dto = new CategorieDTO();
        dto.setId(c.getId());
        dto.setLibelle(c.getLibelle());

        return dto;
    }

    private CategorieDTO toDTOWithSousCategories(CategorieFormationContinues c) {

        CategorieDTO dto = toDTO(c);

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
    }
}