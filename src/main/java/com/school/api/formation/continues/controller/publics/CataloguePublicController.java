package com.school.api.formation.continues.controller.publics;

import com.school.api.formation.continues.dto.CatalogueDTO;
import com.school.api.formation.continues.service.CatalogueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/catalogue")
@RequiredArgsConstructor
public class CataloguePublicController {

    private final CatalogueService service;

    @GetMapping
    public CatalogueDTO getCatalogue() {

        CatalogueDTO dto = new CatalogueDTO();

        dto.setCategories(service.getCatalogue());

        return dto;
    }
}