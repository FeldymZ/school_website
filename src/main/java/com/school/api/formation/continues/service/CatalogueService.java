package com.school.api.formation.continues.service;

import com.school.api.formation.continues.dto.CategorieDTO;
import com.school.api.formation.continues.mapper.CatalogueMapper;
import com.school.api.formation.continues.repository.CategorieFormationContinuesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogueService {

    private final CategorieFormationContinuesRepository repository;
    private final CatalogueMapper mapper;

    public List<CategorieDTO> getCatalogue() {
        return mapper.toCategorieDTOList(repository.findAll());
    }
}