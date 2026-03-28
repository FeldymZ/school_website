package com.school.api.formation.continues.controller.admin;

import com.school.api.formation.continues.dto.DemandeDevisAdminDTO;
import com.school.api.formation.continues.dto.RepondreDemandeDTO;
import com.school.api.formation.continues.service.DemandeDevisContinuesAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/demandes-devis")
@RequiredArgsConstructor
public class DemandeDevisContinuesAdminController {

    private final DemandeDevisContinuesAdminService service;

    @GetMapping
    public Page<DemandeDevisAdminDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getAll(page, size);
    }

    /* =========================
       REPONDRE A UNE DEMANDE
       ========================= */
    @PostMapping("/{id}/repondre")
    public String repondre(
            @PathVariable Long id,
            @ModelAttribute RepondreDemandeDTO dto
    ) {
        service.repondre(id, dto);
        return "Réponse envoyée";
    }
}