package com.school.api.formation.continues.controller.admin;

import com.school.api.formation.continues.dto.CreateFormationContinuesDTO;
import com.school.api.formation.continues.dto.FormationDTO;
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
    public FormationDTO create(
            @RequestParam Long sousCategorieId,
            @ModelAttribute CreateFormationContinuesDTO dto
    ) {
        return service.create(sousCategorieId, dto);
    }

    @PutMapping("/{id}")
    public FormationDTO update(
            @PathVariable Long id,
            @ModelAttribute CreateFormationContinuesDTO dto
    ) {
        return service.update(id, dto);
    }

    @GetMapping
    public Page<FormationDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getAll(page, size);
    }

    @GetMapping("/{id}")
    public FormationDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PatchMapping("/{id}/toggle")
    public FormationDTO toggleStatus(@PathVariable Long id) {
        return service.toggleStatus(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}