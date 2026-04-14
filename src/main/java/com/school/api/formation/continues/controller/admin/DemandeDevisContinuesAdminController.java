package com.school.api.formation.continues.controller.admin;

import com.school.api.formation.continues.dto.*;
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

    /* ================= LISTE ================= */
    @GetMapping
    public Page<DemandeDevisAdminDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getAll(page, size);
    }

    /* ================= DETAIL ================= */
    @GetMapping("/{id}")
    public DemandeDevisAdminDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    /* ================= REPONDRE ================= */
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

    /* ================= COUNT ================= */
    @GetMapping("/count-non-traitees")
    public Long countNonTraitees() {
        return service.countNonTraitees();
    }

    /* ================= DELETE ================= */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Supprimé"
        ));
    }

    @PostMapping("/{id}/cloturer")
    public ResponseEntity<?> cloturer(@PathVariable Long id) {
        service.cloturer(id);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Demande clôturée"
        ));
    }
}