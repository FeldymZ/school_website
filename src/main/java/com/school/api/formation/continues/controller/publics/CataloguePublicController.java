package com.school.api.formation.continues.controller.publics;

import com.school.api.formation.continues.dto.CategorieDTO;
import com.school.api.formation.continues.service.CatalogueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/catalogue")
@RequiredArgsConstructor
public class CataloguePublicController {

    private final CatalogueService service;

    @GetMapping
    public List<CategorieDTO> getCatalogue() {
        return service.getCatalogue();
    }
}