package com.school.api.formation.preinscription.controller;

import com.school.api.formation.preinscription.dto.*;
import com.school.api.formation.preinscription.service.PreinscriptionPeriodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/preinscriptions/config")
@RequiredArgsConstructor
public class PreinscriptionConfigController {

    private final PreinscriptionPeriodeService service;

    /* ===== SESSION ===== */
    @PostMapping("/sessions")
    public void createSession(@Valid @RequestBody SessionRequest req) {
        service.createSession(req);
    }

    /* ===== PERIODE ===== */
    @PostMapping("/periodes")
    public void createPeriode(@Valid @RequestBody PeriodeRequest req) {
        service.createPeriode(req);
    }
}