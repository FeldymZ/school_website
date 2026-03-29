package com.school.api.formation.continues.service;

import com.school.api.formation.continues.dto.SousCategorieDTO;
import com.school.api.formation.continues.entity.*;
import com.school.api.formation.continues.mapper.CatalogueMapper;
import com.school.api.formation.continues.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SousCategorieFormationContinuesService {

    private final SousCategorieFormationContinuesRepository repository;
    private final CategorieFormationContinuesRepository categorieRepository;
    private final CatalogueMapper mapper;

    /* ================= CREATE ================= */

    public SousCategorieDTO create(Long categorieId, String libelle) {

        CategorieFormationContinues categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        SousCategorieFormationContinues sc = new SousCategorieFormationContinues();
        sc.setLibelle(libelle);
        sc.setCategorie(categorie);

        return mapper.toSousCategorieDTO(repository.save(sc));
    }

    /* ================= GET ================= */

    public List<SousCategorieDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toSousCategorieDTO)
                .toList();
    }

    /* ================= DELETE ================= */

    public void delete(Long id) {

        SousCategorieFormationContinues sc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sous-catégorie introuvable"));

        // 🔒 PROTECTION MÉTIER
        if (sc.getFormations() != null && !sc.getFormations().isEmpty()) {
            throw new RuntimeException(
                    "Impossible de supprimer une sous-catégorie contenant des formations"
            );
        }

        repository.delete(sc);
    }
}