package com.school.api.formation.continues.controller.admin;

import com.school.api.formation.continues.dto.DemandeDevisAdminDTO;
import com.school.api.formation.continues.dto.DemandeDevisReponseDTO;
import com.school.api.formation.continues.dto.RepondreDemandeDTO;
import com.school.api.formation.continues.service.DemandeDevisContinuesAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/demandes-devis")
@RequiredArgsConstructor
public class DemandeDevisContinuesAdminController {

    private final DemandeDevisContinuesAdminService service;

    /* ================= LISTE PAGINÉE ================= */

    @GetMapping
    public Page<DemandeDevisAdminDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getAll(page, size);
    }

    /* ================= RÉPONDRE ================= */

    @PostMapping("/{id}/repondre")
    public ResponseEntity<?> repondre(
            @PathVariable Long id,
            @ModelAttribute RepondreDemandeDTO dto
    ) {
        service.repondre(id, dto);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Réponse envoyée"
        ));
    }

    /* ================= HISTORIQUE ================= */

    @GetMapping("/{id}/reponses")
    public List<DemandeDevisReponseDTO> getReponses(@PathVariable Long id) {
        return service.getReponses(id);
    }

    /* ================= 🔥 COUNT NON TRAITÉES ================= */

    @GetMapping("/count-non-traitees")
    public Long countNonTraitees() {
        return service.countNonTraitees();
    }
}