package com.school.api.formation.preinscription.controller;

import com.school.api.formation.preinscription.dto.*;
import com.school.api.formation.preinscription.service.PreinscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/preinscriptions/config")
@RequiredArgsConstructor
public class PreinscriptionConfigController {

    private final PreinscriptionService service;

    /* =========================
       📌 SESSION UNIVERSITAIRE
       ========================= */
    @PostMapping("/sessions")
    public void createSession(@Valid @RequestBody SessionUniversitaireRequest req) {
        service.createSession(req);
    }

    @GetMapping("/sessions")
    public java.util.List<SessionUniversitaireResponse> getSessions() {
        return service.getAllSessions();
    }

    /* =========================
       📌 PERIODES
       ========================= */
    @PostMapping("/periodes")
    public void createPeriode(@Valid @RequestBody PeriodeRequest req) {
        service.createPeriode(req);
    }

    @GetMapping("/periodes")
    public java.util.List<com.school.api.formation.preinscription.dto.PeriodeResponse> getPeriodes() {
        return service.getAllPeriodes();
    }
}