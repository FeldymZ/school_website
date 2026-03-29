package com.school.api.formation.continues.controller.admin;

import com.school.api.formation.continues.dto.SousCategorieDTO;
import com.school.api.formation.continues.service.SousCategorieFormationContinuesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sous-categories")
@RequiredArgsConstructor
public class SousCategorieFormationContinuesAdminController {

    private final SousCategorieFormationContinuesService service;

    /* ================= CREATE ================= */

    @PostMapping
    public SousCategorieDTO create(@RequestBody @Valid SousCategorieDTO request) {
        return service.create(request.getCategorieId(), request.getLibelle());
    }

    /* ================= GET ALL ================= */

    @GetMapping
    public List<SousCategorieDTO> getAll() {
        return service.getAll();
    }

    /* ================= GET BY ID ================= */

    @GetMapping("/{id}")
    public SousCategorieDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    /* ================= UPDATE ================= */

    @PutMapping("/{id}")
    public SousCategorieDTO update(
            @PathVariable Long id,
            @RequestBody @Valid SousCategorieDTO request
    ) {
        return service.update(id, request.getLibelle(), request.getCategorieId());
    }

    /* ================= DELETE ================= */

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}