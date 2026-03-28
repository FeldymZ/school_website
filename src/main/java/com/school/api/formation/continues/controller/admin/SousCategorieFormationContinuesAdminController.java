package com.school.api.formation.continues.controller.admin;

import com.school.api.formation.continues.dto.SousCategorieDTO;
import com.school.api.formation.continues.service.SousCategorieFormationContinuesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sous-categories")
@RequiredArgsConstructor
public class SousCategorieFormationContinuesAdminController {

    private final SousCategorieFormationContinuesService service;

    @PostMapping
    public SousCategorieDTO create(@RequestBody SousCategorieDTO request) {
        return service.create(request.getId(), request.getLibelle());
    }

    @GetMapping
    public List<SousCategorieDTO> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}