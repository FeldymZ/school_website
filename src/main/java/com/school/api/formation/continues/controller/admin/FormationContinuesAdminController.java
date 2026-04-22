package com.school.api.formation.continues.controller.admin;

import com.school.api.formation.continues.dto.CreateFormationContinuesDTO;
import com.school.api.formation.continues.dto.FormationDTO;
import com.school.api.formation.continues.service.FormationContinuesService;
import com.school.api.auth.audit.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/formations")
@RequiredArgsConstructor
public class FormationContinuesAdminController {

    private final FormationContinuesService service;

    @AuditLog(action = "CREATION_FORMATION", target = "#sousCategorieId.toString()", failureAction = "CREATION_FORMATION_ECHEC")
    @PostMapping
    public FormationDTO create(
            @RequestParam Long sousCategorieId,
            @ModelAttribute CreateFormationContinuesDTO dto
    ) {
        return service.create(sousCategorieId, dto);
    }

    @AuditLog(action = "MODIFICATION_FORMATION", target = "#id.toString()", failureAction = "MODIFICATION_FORMATION_ECHEC")
    @PutMapping("/{id}")
    public FormationDTO update(
            @PathVariable Long id,
            @ModelAttribute CreateFormationContinuesDTO dto
    ) {
        return service.update(id, dto);
    }

    @AuditLog(action = "CONSULTATION_FORMATIONS")
    @GetMapping
    public Page<FormationDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getAll(page, size);
    }

    @AuditLog(action = "CONSULTATION_FORMATION", target = "#id.toString()")
    @GetMapping("/{id}")
    public FormationDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @AuditLog(action = "TOGGLE_STATUT_FORMATION", target = "#id.toString()")
    @PatchMapping("/{id}/toggle")
    public FormationDTO toggleStatus(@PathVariable Long id) {
        return service.toggleStatus(id);
    }

    @AuditLog(action = "SUPPRESSION_FORMATION", target = "#id.toString()", failureAction = "SUPPRESSION_FORMATION_ECHEC")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}