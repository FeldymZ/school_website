package com.school.api.formation.preinscription.controller;

import com.school.api.auth.audit.AuditLog;
import com.school.api.formation.preinscription.dto.*;
import com.school.api.formation.preinscription.entity.*;
import com.school.api.formation.preinscription.service.PreinscriptionService;
import com.school.api.formation.preinscription.service.PreinscriptionPeriodeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/preinscriptions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class PreinscriptionAdminController {

    private final PreinscriptionService service;
    private final PreinscriptionPeriodeService periodeService;

    /* ════════════════════════════════
       📌 DEMANDES
       ════════════════════════════════ */

    @AuditLog(action = "CONSULTATION_PREINSCRIPTIONS")
    @GetMapping
    public List<PreinscriptionDemandeResponse> getAll() {
        return service.getAll();
    }

    @AuditLog(action = "CONSULTATION_PREINSCRIPTIONS_PAR_STATUT", target = "#statut.toString()")
    @GetMapping("/statut/{statut}")
    public List<PreinscriptionDemandeResponse> getByStatut(
            @PathVariable StatutDemande statut
    ) {
        return service.getByStatut(statut);
    }

    @AuditLog(action = "CONSULTATION_PREINSCRIPTIONS_PAR_FORMATION", target = "#formationId.toString()")
    @GetMapping("/formation/{formationId}")
    public List<PreinscriptionDemandeResponse> getByFormation(
            @PathVariable Long formationId
    ) {
        return service.getByFormation(formationId);
    }

    @AuditLog(action = "CONSULTATION_PREINSCRIPTION", target = "#id.toString()")
    @GetMapping("/{id}")
    public PreinscriptionDemandeResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @AuditLog(action = "VALIDATION_PREINSCRIPTION", target = "#id.toString()")
    @PostMapping("/{id}/valider")
    public PreinscriptionDemandeResponse validate(@PathVariable Long id) {
        return service.validate(id);
    }

    @AuditLog(action = "REJET_PREINSCRIPTION", target = "#id.toString()")
    @PostMapping("/{id}/rejeter")
    public void reject(@PathVariable Long id) {
        service.reject(id);
    }

    /* ════════════════════════════════
       📌 SESSIONS UNIVERSITAIRES
       ════════════════════════════════ */

    @AuditLog(action = "CREATION_SESSION_UNIVERSITAIRE")
    @PostMapping("/sessions")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public void createSession(
            @Valid @RequestBody SessionUniversitaireRequest req
    ) {
        periodeService.createSession(req);
    }

    @AuditLog(action = "CONSULTATION_SESSIONS")
    @GetMapping("/sessions")
    public List<SessionUniversitaire> getSessions() {
        return periodeService.getAllSessions();
    }

    @AuditLog(action = "SUPPRESSION_SESSION")
    @DeleteMapping("/sessions/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public void deleteSession(@PathVariable Long id) {
        periodeService.deleteSession(id);
    }

    /* ════════════════════════════════
       📌 PÉRIODES DE PRÉINSCRIPTION
       ════════════════════════════════ */

    @AuditLog(action = "CREATION_PERIODE_PREINSCRIPTION")
    @PostMapping("/periodes")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public void createPeriode(
            @Valid @RequestBody PeriodeRequest req
    ) {
        periodeService.createPeriode(req);
    }

    @AuditLog(action = "CONSULTATION_PERIODES")
    @GetMapping("/periodes")
    public List<PreinscriptionPeriode> getPeriodes() {
        return periodeService.getAll();
    }

    @AuditLog(action = "SUPPRESSION_PERIODE")
    @DeleteMapping("/periodes/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public void deletePeriode(@PathVariable Long id) {
        periodeService.deletePeriode(id);
    }

    @AuditLog(action = "MODIFICATION_SESSION", target = "#id.toString()")
    @PutMapping("/sessions/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public void updateSession(
            @PathVariable Long id,
            @RequestBody SessionUniversitaireRequest req
    ) {
        periodeService.updateSession(id, req);
    }

    @AuditLog(action = "MODIFICATION_PERIODE", target = "#id.toString()")
    @PutMapping("/periodes/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public void updatePeriode(
            @PathVariable Long id,
            @Valid @RequestBody PeriodeRequest req
    ) {
        periodeService.updatePeriode(id, req);
    }

    @AuditLog(action = "DESACTIVATION_PERIODE", target = "#id.toString()")
    @PutMapping("/periodes/{id}/desactiver")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public void deactivatePeriode(@PathVariable Long id) {
        periodeService.deactivatePeriode(id);
    }
}