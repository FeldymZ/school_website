package com.school.api.formation.preinscription.controller;

import com.school.api.formation.preinscription.dto.*;
import com.school.api.formation.preinscription.service.PreinscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/preinscriptions")
@RequiredArgsConstructor
public class PreinscriptionPublicController {

    private final PreinscriptionService service;

    /* =========================
       📌 SESSION ACTIVE
       ========================= */
    @GetMapping("/session")
    public SessionPublicResponse getSession() {
        return service.getActiveSession();
    }

    /* =========================
       📌 STATUT (legacy optionnel)
       ========================= */
    @GetMapping("/statut")
    public boolean isOuverte() {
        return service.isSessionOuverte();
    }

    /* =========================
       📌 SOUMISSION
       ========================= */
    @PostMapping
    public PreinscriptionDemandeResponse submit(
            @Valid @RequestBody PreinscriptionDemandeRequest request
    ) {
        return service.submit(request);
    }
}