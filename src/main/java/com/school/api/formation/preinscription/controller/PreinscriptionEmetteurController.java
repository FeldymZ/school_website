package com.school.api.formation.preinscription.controller;

import com.school.api.auth.audit.AuditLog;
import com.school.api.formation.preinscription.entity.PreinscriptionEmetteur;
import com.school.api.formation.preinscription.service.PreinscriptionEmetteurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/preinscriptions/emetteurs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class PreinscriptionEmetteurController {

    private final PreinscriptionEmetteurService service;

    /* =========================
       📌 GET ALL
       ========================= */
    @AuditLog(action = "CONSULTATION_EMETTEURS")
    @GetMapping
    public List<PreinscriptionEmetteur> getAll() {
        return service.getAll();
    }

    /* =========================
       📌 CREATE
       ========================= */
    @AuditLog(action = "CREATION_EMETTEUR", target = "#nom")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void create(
            @RequestParam String nom,
            @RequestParam String fonction,
            @RequestParam MultipartFile signature
    ) {
        service.create(nom, fonction, signature);
    }

    /* =========================
       📌 ACTIVATE
       ========================= */
    @AuditLog(action = "ACTIVATION_EMETTEUR", target = "#id.toString()")
    @PutMapping("/{id}/activer")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public void activate(@PathVariable Long id) {
        service.activate(id);
    }
}