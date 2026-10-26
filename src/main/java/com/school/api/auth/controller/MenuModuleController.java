package com.school.api.auth.controller;

import com.school.api.auth.audit.AuditLog;
import com.school.api.auth.dto.MenuModuleDTO;
import com.school.api.auth.dto.MenuModuleRequest;
import com.school.api.auth.dto.OrdreModulesRequest;
import com.school.api.auth.service.MenuModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MenuModuleController {

    private final MenuModuleService service;

    /* ===== Menu (tous les utilisateurs connectés) ===== */

    @GetMapping("/api/menu-modules")
    public List<MenuModuleDTO> actifs() {
        return service.actifs();
    }

    /* ===== Gestion (SUPERADMIN) ===== */

    @GetMapping("/api/admin/menu-modules")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public List<MenuModuleDTO> tous() {
        return service.tous();
    }

    @AuditLog(action = "CREATION_MODULE", target = "#request.libelle", failureAction = "CREATION_MODULE_ECHEC")
    @PostMapping("/api/admin/menu-modules")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public MenuModuleDTO creer(@RequestBody MenuModuleRequest request) {
        return service.creer(request);
    }

    @AuditLog(action = "MODIFICATION_MODULE", target = "#request.libelle", failureAction = "MODIFICATION_MODULE_ECHEC")
    @PutMapping("/api/admin/menu-modules/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public MenuModuleDTO modifier(@PathVariable Long id, @RequestBody MenuModuleRequest request) {
        return service.modifier(id, request);
    }

    @PatchMapping("/api/admin/menu-modules/{id}/actif")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public MenuModuleDTO changerActif(@PathVariable Long id, @RequestParam boolean valeur) {
        return service.changerActif(id, valeur);
    }

    @PutMapping("/api/admin/menu-modules/ordre")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> reordonner(@RequestBody OrdreModulesRequest request) {
        service.reordonner(request.ids());
        return ResponseEntity.noContent().build();
    }

    @AuditLog(action = "SUPPRESSION_MODULE", target = "#id", failureAction = "SUPPRESSION_MODULE_ECHEC")
    @DeleteMapping("/api/admin/menu-modules/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        service.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}