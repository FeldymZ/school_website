package com.school.api.formation.continues.controller.publics;

import com.school.api.formation.continues.dto.SousCategorieDTO;
import com.school.api.formation.continues.service.SousCategorieFormationContinuesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/sous-categories")
@RequiredArgsConstructor
public class SousCategorieFormationContinuesPublicController {

    private final SousCategorieFormationContinuesService service;

    @GetMapping
    public List<SousCategorieDTO> getAll(
            @RequestParam(required = false) Long categorieId
    ) {
        if (categorieId != null) {
            return service.getByCategorieId(categorieId);
        }
        return service.getAll();
    }
}