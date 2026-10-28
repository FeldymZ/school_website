package com.school.api.formation.continues.controller.admin;

import com.school.api.auth.audit.AuditLog;
import com.school.api.auth.security.RequiresMenuAccess; // ⚠️ même import que dans SessionFormationAdminController
import com.school.api.formation.continues.dto.PageSessionsFormationDTO;
import com.school.api.formation.continues.service.PageSessionsFormationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Back office — textes et image de la page publique « Sessions de formation »
 * (même accès que « Agenda de formation »)
 */
@RestController
@RequestMapping("/api/admin/sessions-formation-page")
@RequiredArgsConstructor
public class PageSessionsFormationAdminController {

    private final PageSessionsFormationService service;

    @GetMapping
    @RequiresMenuAccess("FORMATIONS_SESSIONS_AGENDA")
    public PageSessionsFormationDTO lire() {
        return service.lire();
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiresMenuAccess("FORMATIONS_SESSIONS_AGENDA")
    @AuditLog(action = "MODIFICATION_PAGE_SESSIONS", target = "#titre", failureAction = "MODIFICATION_PAGE_SESSIONS_ECHEC")
    public PageSessionsFormationDTO modifier(
            @RequestParam(required = false) String badge,
            @RequestParam String titre,
            @RequestParam(required = false) String sousTitre,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(defaultValue = "false") boolean supprimerImage,
            Authentication auth
    ) {
        return service.modifier(badge, titre, sousTitre, image, supprimerImage, auth != null ? auth.getName() : null);
    }
}