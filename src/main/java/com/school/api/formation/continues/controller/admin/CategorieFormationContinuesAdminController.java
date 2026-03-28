package com.school.api.formation.continues.controller.admin;

import com.school.api.formation.continues.dto.CategorieDTO;
import com.school.api.formation.continues.service.CategorieFormationContinuesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategorieFormationContinuesAdminController {

    private final CategorieFormationContinuesService service;

    /* ================= CREATE ================= */

    @PostMapping
    public CategorieDTO create(@RequestBody CategorieDTO request) {
        return service.create(request.getLibelle());
    }

    /* ================= GET ================= */

    @GetMapping
    public List<CategorieDTO> getAll() {
        return service.getAll();
    }

    /* ================= UPDATE ================= */

    @PutMapping("/{id}")
    public CategorieDTO update(
            @PathVariable Long id,
            @RequestBody CategorieDTO request
    ) {
        return service.update(id, request.getLibelle());
    }

    /* ================= DELETE ================= */

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}