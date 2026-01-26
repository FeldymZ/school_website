package com.school.api.vieetudiante.visiteentreprise.controller;

import com.school.api.vieetudiante.visiteentreprise.dto.VisiteEntrepriseResponse;
import com.school.api.vieetudiante.visiteentreprise.service.VisiteEntrepriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/vie-etudiante/visites-entreprise")
public class VisiteEntreprisePublicController {

    private final VisiteEntrepriseService service;

    @GetMapping
    public List<VisiteEntrepriseResponse> getAll() {
        return service.getAllPublished();
    }

    @GetMapping("/{id}")
    public VisiteEntrepriseResponse getById(@PathVariable Long id) {
        return service.getPublishedById(id);
    }
}
