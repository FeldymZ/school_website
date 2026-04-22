package com.school.api.formation.continues.controller.admin;

import com.school.api.formation.continues.dto.*;
import com.school.api.formation.continues.service.DemandeDevisContinuesAdminService;
import com.school.api.auth.audit.AuditLog;
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

    @AuditLog(action = "CONSULTATION_DEMANDES_DEVIS")
    @GetMapping
    public Page<DemandeDevisAdminDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getAll(page, size);
    }

    @AuditLog(action = "CONSULTATION_DEMANDE_DEVIS", target = "#id.toString()")
    @GetMapping("/{id}")
    public DemandeDevisAdminDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @AuditLog(action = "REPONSE_DEMANDE_DEVIS", target = "#id.toString()", failureAction = "REPONSE_DEMANDE_DEVIS_ECHEC")
    @PostMapping("/{id}/repondre")
    public ResponseEntity<?> repondre(
            @PathVariable Long id,
            @ModelAttribute RepondreDemandeDTO dto
    ) {
        service.repondre(id, dto);
        return ResponseEntity.ok(Map.of("success", true, "message", "Réponse envoyée"));
    }

    @AuditLog(action = "CONSULTATION_REPONSES_DEMANDE", target = "#id.toString()")
    @GetMapping("/{id}/reponses")
    public List<DemandeDevisReponseDTO> getReponses(@PathVariable Long id) {
        return service.getReponses(id);
    }

    @AuditLog(action = "COMPTAGE_DEMANDES_NON_TRAITEES")
    @GetMapping("/count-non-traitees")
    public Long countNonTraitees() {
        return service.countNonTraitees();
    }

    @AuditLog(action = "SUPPRESSION_DEMANDE_DEVIS", target = "#id.toString()", failureAction = "SUPPRESSION_DEMANDE_ECHEC")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Supprimé"));
    }

    @AuditLog(action = "CLOTURE_DEMANDE_DEVIS", target = "#id.toString()", failureAction = "CLOTURE_DEMANDE_ECHEC")
    @PostMapping("/{id}/cloturer")
    public ResponseEntity<?> cloturer(@PathVariable Long id) {
        service.cloturer(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Demande clôturée"));
    }
}