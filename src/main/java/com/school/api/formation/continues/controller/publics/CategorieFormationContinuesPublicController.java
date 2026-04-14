package com.school.api.formation.continues.controller.publics;

import com.school.api.formation.continues.dto.CategorieDTO;
import com.school.api.formation.continues.service.CategorieFormationContinuesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/categories")
@RequiredArgsConstructor
public class CategorieFormationContinuesPublicController {

    private final CategorieFormationContinuesService service;

    @GetMapping
    public List<CategorieDTO> getAll() {
        return service.getAll(); // déjà avec sous-catégories 👍
    }
}