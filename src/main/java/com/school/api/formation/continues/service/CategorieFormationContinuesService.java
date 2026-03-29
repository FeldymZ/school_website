package com.school.api.formation.continues.service;

import com.school.api.formation.continues.dto.CategorieDTO;
import com.school.api.formation.continues.entity.CategorieFormationContinues;
import com.school.api.formation.continues.mapper.CatalogueMapper;
import com.school.api.formation.continues.repository.CategorieFormationContinuesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorieFormationContinuesService {

    private final CategorieFormationContinuesRepository repository;
    private final CatalogueMapper mapper;

    /* ================= CREATE ================= */

    public CategorieDTO create(String libelle) {

        CategorieFormationContinues c = new CategorieFormationContinues();
        c.setLibelle(libelle);

        return mapper.toCategorieDTO(repository.save(c));
    }

    /* ================= GET ================= */

    public List<CategorieDTO> getAll() {

        // 🔥 FIX LAZY LOADING
        List<CategorieFormationContinues> categories =
                repository.findAllWithSousCategories();

        return mapper.toCategorieDTOList(categories);
    }

    /* ================= UPDATE ================= */

    public CategorieDTO update(Long id, String libelle) {

        CategorieFormationContinues c = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        c.setLibelle(libelle);

        return mapper.toCategorieDTO(repository.save(c));
    }

    /* ================= DELETE ================= */

    public void delete(Long id) {
        repository.deleteById(id);
    }
}