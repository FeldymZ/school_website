package com.school.api.formation.preinscription.controller;

import com.school.api.auth.audit.AuditLog;
import com.school.api.formation.preinscription.entity.PreinscriptionPeriode;
import com.school.api.formation.preinscription.service.PreinscriptionPeriodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/preinscriptions/periodes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class PreinscriptionPeriodeController {

    private final PreinscriptionPeriodeService service;

    /* ================= GET ALL ================= */
    @GetMapping
    public List<PreinscriptionPeriode> getAll() {
        return service.getAll();
    }

    /* ================= DELETE ================= */
    @AuditLog(action = "SUPPRESSION_PERIODE", target = "#id.toString()")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}