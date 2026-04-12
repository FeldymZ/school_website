package com.school.api.formation.continues.service;

import com.school.api.formation.continues.dto.SousCategorieDTO;
import com.school.api.formation.continues.entity.*;
import com.school.api.formation.continues.mapper.CatalogueMapper;
import com.school.api.formation.continues.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SousCategorieFormationContinuesService {

    private final SousCategorieFormationContinuesRepository repository;
    private final CategorieFormationContinuesRepository categorieRepository;
    private final CatalogueMapper mapper;

    /* ================= CREATE ================= */

    public SousCategorieDTO create(Long categorieId, String libelle) {

        if (libelle == null || libelle.trim().isEmpty()) {
            throw new RuntimeException("Le libellé est obligatoire");
        }

        CategorieFormationContinues categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        SousCategorieFormationContinues sc = new SousCategorieFormationContinues();
        sc.setLibelle(libelle.trim());
        sc.setCategorie(categorie);

        return mapper.toSousCategorieDTO(repository.save(sc));
    }

    /* ================= GET ALL ================= */

    @Transactional(readOnly = true) // 🔥 FIX CRITIQUE
    public List<SousCategorieDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toSousCategorieDTO)
                .toList();
    }

    /* ================= GET BY ID ================= */

    public SousCategorieDTO getById(Long id) {
        SousCategorieFormationContinues sc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sous-catégorie introuvable"));

        return mapper.toSousCategorieDTO(sc);
    }

    /* ================= UPDATE ================= */

    public SousCategorieDTO update(Long id, String libelle, Long categorieId) {

        SousCategorieFormationContinues sc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sous-catégorie introuvable"));

        if (libelle == null || libelle.trim().isEmpty()) {
            throw new RuntimeException("Le libellé est obligatoire");
        }

        CategorieFormationContinues categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        sc.setLibelle(libelle.trim());
        sc.setCategorie(categorie);

        return mapper.toSousCategorieDTO(repository.save(sc));
    }

    /* ================= DELETE ================= */

    @Transactional // 🔥 IMPORTANT
    public void delete(Long id) {

        SousCategorieFormationContinues sc = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sous-catégorie introuvable"));

        // 🔥 FIX : on force le chargement dans une transaction
        if (sc.getFormations() != null && !sc.getFormations().isEmpty()) {
            throw new RuntimeException(
                    "Impossible de supprimer cette sous-catégorie car elle contient des formations"
            );
        }

        repository.delete(sc);
    }
}