package com.school.api.formation.continues.controller.publics;

import com.school.api.formation.continues.dto.FormationSessionsPublicDTO;
import com.school.api.formation.continues.dto.InscriptionSessionRequest;
import com.school.api.formation.continues.service.InscriptionSessionService;
import com.school.api.formation.continues.service.SessionFormationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Site public — onglet « Session de formation »
 */
@RestController
@RequestMapping("/api/public/sessions-formation")
@RequiredArgsConstructor
public class SessionFormationPublicController {

    private final SessionFormationService sessionService;
    private final InscriptionSessionService inscriptionService;

    /** Formations qui ont au moins une session ouverte */
    @GetMapping
    public List<FormationSessionsPublicDTO> formations() {
        return sessionService.formationsAvecSessions();
    }

    /** Une formation et ses sessions ouvertes */
    @GetMapping("/{slug}")
    public FormationSessionsPublicDTO formation(@PathVariable String slug) {
        return sessionService.formationParSlug(slug);
    }

    /** Inscription à une session */
    @PostMapping("/{sessionId}/inscriptions")
    public ResponseEntity<Map<String, String>> inscrire(@PathVariable Long sessionId,
                                                        @RequestBody InscriptionSessionRequest request) {
        inscriptionService.inscrire(sessionId, request);
        return ResponseEntity.ok(Map.of("message", "Votre inscription a bien été envoyée"));
    }
}