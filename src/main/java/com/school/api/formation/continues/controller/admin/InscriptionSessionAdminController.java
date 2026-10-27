package com.school.api.formation.continues.controller.admin;

import com.school.api.auth.audit.AuditLog;
import com.school.api.auth.security.RequiresMenuAccess; // ⚠️ même import que dans vos autres contrôleurs
import com.school.api.formation.continues.dto.InscriptionSessionDTO;
import com.school.api.formation.continues.dto.RefusInscriptionRequest;
import com.school.api.formation.continues.dto.SessionFormationDTO;
import com.school.api.formation.continues.service.InscriptionSessionService;
import com.school.api.formation.continues.service.SessionFormationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Back office — « Demandes de participation »
 */
@RestController
@RequestMapping("/api/admin/inscriptions-formation")
@RequiredArgsConstructor
public class InscriptionSessionAdminController {

    private final InscriptionSessionService service;
    private final SessionFormationService sessionService;

    @GetMapping
    @RequiresMenuAccess("FORMATIONS_SESSIONS_PARTICIPATIONS")
    public Page<InscriptionSessionDTO> lister(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) Long sessionId
    ) {
        return service.lister(page, size, statut, sessionId);
    }

    @GetMapping("/stats")
    @RequiresMenuAccess("FORMATIONS_SESSIONS_PARTICIPATIONS")
    public Map<String, Long> stats() {
        return service.stats();
    }

    /** Sessions pour le filtre */
    @GetMapping("/sessions")
    @RequiresMenuAccess("FORMATIONS_SESSIONS_PARTICIPATIONS")
    public List<SessionFormationDTO> sessions() {
        return sessionService.toutes();
    }

    /** Tous les inscrits d'une session (export) */
    @GetMapping("/session/{sessionId}")
    @RequiresMenuAccess("FORMATIONS_SESSIONS_PARTICIPATIONS")
    public List<InscriptionSessionDTO> parSession(@PathVariable Long sessionId) {
        return service.parSession(sessionId);
    }

    @PatchMapping("/{id}/confirmer")
    @RequiresMenuAccess("FORMATIONS_SESSIONS_PARTICIPATIONS")
    @AuditLog(action = "CONFIRMATION_PARTICIPATION", target = "#id", failureAction = "CONFIRMATION_PARTICIPATION_ECHEC")
    public InscriptionSessionDTO confirmer(@PathVariable Long id, Authentication auth) {
        return service.confirmer(id, auth != null ? auth.getName() : null);
    }

    @PatchMapping("/{id}/refuser")
    @RequiresMenuAccess("FORMATIONS_SESSIONS_PARTICIPATIONS")
    @AuditLog(action = "REFUS_PARTICIPATION", target = "#id", failureAction = "REFUS_PARTICIPATION_ECHEC")
    public InscriptionSessionDTO refuser(@PathVariable Long id,
                                         @RequestBody(required = false) RefusInscriptionRequest request,
                                         Authentication auth) {
        return service.refuser(id, request != null ? request.motif() : null, auth != null ? auth.getName() : null);
    }

    @DeleteMapping("/{id}")
    @RequiresMenuAccess("FORMATIONS_SESSIONS_PARTICIPATIONS")
    @AuditLog(action = "SUPPRESSION_PARTICIPATION", target = "#id", failureAction = "SUPPRESSION_PARTICIPATION_ECHEC")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        service.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}