package com.school.api.formation.continues.controller.admin;

import com.school.api.auth.audit.AuditLog;
import com.school.api.formation.continues.dto.DevisEmetteurResponse;
import com.school.api.formation.continues.service.DevisEmetteurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Validateurs des devis (même fonctionnement que les émetteurs des préinscriptions) */
@RestController
@RequestMapping("/api/admin/devis/emetteurs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class DevisEmetteurController {

    private final DevisEmetteurService service;

    /* ================= GET ALL ================= */
    @AuditLog(action = "CONSULTATION_VALIDATEURS_DEVIS")
    @GetMapping
    public List<DevisEmetteurResponse> getAll() {
        return service.getAll();
    }

    /* ================= PAR DÉFAUT ================= */
    @GetMapping("/actif")
    public DevisEmetteurResponse getActive() {
        return service.getActive();
    }

    /* ================= CREATE ================= */
    @AuditLog(action = "CREATION_VALIDATEUR_DEVIS", target = "#nom")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void create(
            @RequestParam String nom,
            @RequestParam String fonction,
            @RequestParam MultipartFile signature,
            @RequestParam(required = false) MultipartFile cachet
    ) {
        service.create(nom, fonction, signature, cachet);
    }

    /* ================= UPDATE ================= */
    @AuditLog(action = "MODIFICATION_VALIDATEUR_DEVIS", target = "#id.toString()")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void update(
            @PathVariable Long id,
            @RequestParam String nom,
            @RequestParam String fonction,
            @RequestParam(required = false) MultipartFile signature,
            @RequestParam(required = false) MultipartFile cachet,
            @RequestParam(defaultValue = "false") boolean supprimerCachet
    ) {
        service.update(id, nom, fonction, signature, cachet, supprimerCachet);
    }

    /* ================= DELETE ================= */
    @AuditLog(action = "SUPPRESSION_VALIDATEUR_DEVIS", target = "#id.toString()")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    /* ================= DÉFINIR PAR DÉFAUT (SUPERADMIN) ================= */
    @AuditLog(action = "ACTIVATION_VALIDATEUR_DEVIS", target = "#id.toString()")
    @PutMapping("/{id}/activer")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public void activate(@PathVariable Long id) {
        service.activate(id);
    }
}