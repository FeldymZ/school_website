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

    public SousCategorieDTO create(Long categorieId, String libelle) {

        CategorieFormationContinues categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        SousCategorieFormationContinues sc = new SousCategorieFormationContinues();
        sc.setLibelle(libelle);
        sc.setCategorie(categorie);

        return mapper.toSousCategorieDTO(repository.save(sc));
    }

    public List<SousCategorieDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toSousCategorieDTO)
                .toList();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}