package com.school.api.formation.continues.controller.admin;

import com.school.api.auth.audit.AuditLog;
import com.school.api.auth.security.RequiresMenuAccess; // ⚠️ même import que dans vos autres contrôleurs
import com.school.api.formation.continues.dto.SessionFormationDTO;
import com.school.api.formation.continues.dto.SessionFormationRequest;
import com.school.api.formation.continues.service.SessionFormationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Back office — « Agenda de formation »
 */
@RestController
@RequestMapping("/api/admin/sessions-formation")
@RequiredArgsConstructor
public class SessionFormationAdminController {

    private final SessionFormationService service;

    @GetMapping
    @RequiresMenuAccess("FORMATIONS_SESSIONS_AGENDA")
    public List<SessionFormationDTO> toutes() {
        return service.toutes();
    }

    /** Liste des formations continues actives (pour le formulaire) */
    @GetMapping("/formations")
    @RequiresMenuAccess("FORMATIONS_SESSIONS_AGENDA")
    public List<Map<String, Object>> formations() {
        return service.formationsDisponibles();
    }

    @PostMapping
    @RequiresMenuAccess("FORMATIONS_SESSIONS_AGENDA")
    @AuditLog(action = "CREATION_SESSION_FORMATION", target = "#request.formationId", failureAction = "CREATION_SESSION_FORMATION_ECHEC")
    public SessionFormationDTO creer(@RequestBody SessionFormationRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}")
    @RequiresMenuAccess("FORMATIONS_SESSIONS_AGENDA")
    @AuditLog(action = "MODIFICATION_SESSION_FORMATION", target = "#id", failureAction = "MODIFICATION_SESSION_FORMATION_ECHEC")
    public SessionFormationDTO modifier(@PathVariable Long id, @RequestBody SessionFormationRequest request) {
        return service.modifier(id, request);
    }

    @PatchMapping("/{id}/active")
    @RequiresMenuAccess("FORMATIONS_SESSIONS_AGENDA")
    @AuditLog(action = "PUBLICATION_SESSION_FORMATION", target = "#id", failureAction = "PUBLICATION_SESSION_FORMATION_ECHEC")
    public SessionFormationDTO changerActive(@PathVariable Long id, @RequestParam boolean valeur) {
        return service.changerActive(id, valeur);
    }

    @DeleteMapping("/{id}")
    @RequiresMenuAccess("FORMATIONS_SESSIONS_AGENDA")
    @AuditLog(action = "SUPPRESSION_SESSION_FORMATION", target = "#id", failureAction = "SUPPRESSION_SESSION_FORMATION_ECHEC")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        service.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}