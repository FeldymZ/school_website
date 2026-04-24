package com.school.api.formation.preinscription.controller;

import com.school.api.auth.audit.AuditLog;
import com.school.api.formation.preinscription.entity.SessionUniversitaire;
import com.school.api.formation.preinscription.service.SessionUniversitaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/preinscriptions/sessions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class SessionUniversitaireController {

    private final SessionUniversitaireService service;

    /* ================= GET ALL ================= */
    @GetMapping
    public List<SessionUniversitaire> getAll() {
        return service.getAll();
    }

    /* ================= DELETE ================= */
    @AuditLog(action = "SUPPRESSION_SESSION", target = "#id.toString()")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}