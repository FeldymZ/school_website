package com.school.api.formation.continues.controller.admin;

import com.school.api.formation.continues.dto.CategorieDTO;
import com.school.api.formation.continues.service.CategorieFormationContinuesService;
import com.school.api.auth.audit.AuditLog;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategorieFormationContinuesAdminController {

    private final CategorieFormationContinuesService service;

    @AuditLog(action = "CREATION_CATEGORIE", target = "#request.libelle", failureAction = "CREATION_CATEGORIE_ECHEC")
    @PostMapping
    public CategorieDTO create(@RequestBody @Valid CategorieDTO request) {
        return service.create(request.getLibelle());
    }

    @AuditLog(action = "CONSULTATION_CATEGORIES")
    @GetMapping
    public List<CategorieDTO> getAll() {
        return service.getAll();
    }

    @AuditLog(action = "MODIFICATION_CATEGORIE", target = "#id.toString()", failureAction = "MODIFICATION_CATEGORIE_ECHEC")
    @PutMapping("/{id}")
    public CategorieDTO update(
            @PathVariable Long id,
            @RequestBody @Valid CategorieDTO request
    ) {
        return service.update(id, request.getLibelle());
    }

    @AuditLog(action = "SUPPRESSION_CATEGORIE", target = "#id.toString()", failureAction = "SUPPRESSION_CATEGORIE_ECHEC")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @AuditLog(action = "CONSULTATION_CATEGORIE", target = "#id.toString()")
    @GetMapping("/{id}")
    public CategorieDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }
}