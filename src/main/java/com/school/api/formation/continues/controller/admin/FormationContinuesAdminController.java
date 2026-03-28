package com.school.api.formation.continues.controller.admin;

import com.school.api.formation.continues.dto.CreateFormationContinuesDTO;
import com.school.api.formation.continues.entity.FormationContinues;
import com.school.api.formation.continues.service.FormationContinuesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/formations")
@RequiredArgsConstructor
public class FormationContinuesAdminController {

    private final FormationContinuesService service;

    @PostMapping
    public FormationContinues create(
            @RequestParam Long sousCategorieId,
            @ModelAttribute CreateFormationContinuesDTO dto
    ) {
        return service.create(sousCategorieId, dto);
    }

    @PutMapping("/{id}")
    public FormationContinues update(
            @PathVariable Long id,
            @ModelAttribute CreateFormationContinuesDTO dto
    ) {
        return service.update(id, dto);
    }

    @GetMapping
    public Page<FormationContinues> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getAll(page, size);
    }

    @GetMapping("/{id}")
    public FormationContinues getById(@PathVariable Long id) {
        return service.getById(id);
    }

    /* 🔥 SEARCH */
    @GetMapping("/search")
    public FormationContinues searchByReference(@RequestParam Integer reference) {
        return service.getByReference(reference);
    }

    /* 🔥 FILTER */
    @GetMapping("/filter")
    public Page<FormationContinues> filter(
            @RequestParam(required = false) Long categorieId,
            @RequestParam(required = false) Long sousCategorieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.filter(categorieId, sousCategorieId, page, size);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}