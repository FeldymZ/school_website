package com.school.api.formation.continues.controller.admin;

import com.school.api.formation.continues.dto.SousCategorieDTO;
import com.school.api.formation.continues.service.SousCategorieFormationContinuesService;
import com.school.api.auth.audit.AuditLog;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sous-categories")
@RequiredArgsConstructor
public class SousCategorieFormationContinuesAdminController {

    private final SousCategorieFormationContinuesService service;

    @AuditLog(action = "CREATION_SOUS_CATEGORIE", target = "#request.libelle", failureAction = "CREATION_SOUS_CATEGORIE_ECHEC")
    @PostMapping
    public SousCategorieDTO create(@RequestBody @Valid SousCategorieDTO request) {
        return service.create(request.getCategorieId(), request.getLibelle());
    }

    @AuditLog(action = "CONSULTATION_SOUS_CATEGORIES")
    @GetMapping
    public List<SousCategorieDTO> getAll() {
        return service.getAll();
    }

    @AuditLog(action = "CONSULTATION_SOUS_CATEGORIE", target = "#id.toString()")
    @GetMapping("/{id}")
    public SousCategorieDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @AuditLog(action = "MODIFICATION_SOUS_CATEGORIE", target = "#id.toString()", failureAction = "MODIFICATION_SOUS_CATEGORIE_ECHEC")
    @PutMapping("/{id}")
    public SousCategorieDTO update(
            @PathVariable Long id,
            @RequestBody @Valid SousCategorieDTO request
    ) {
        return service.update(id, request.getLibelle(), request.getCategorieId());
    }

    @AuditLog(action = "SUPPRESSION_SOUS_CATEGORIE", target = "#id.toString()", failureAction = "SUPPRESSION_SOUS_CATEGORIE_ECHEC")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}