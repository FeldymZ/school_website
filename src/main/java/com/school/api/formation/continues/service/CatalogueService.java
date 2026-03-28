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
public class CatalogueService {

    private final CategorieFormationContinuesRepository categorieRepository;
    private final CatalogueMapper catalogueMapper;

    public List<CategorieDTO> getCatalogue() {

        List<CategorieFormationContinues> categories = categorieRepository.findAll();

        return catalogueMapper.toCategorieDTOList(categories);
    }
}