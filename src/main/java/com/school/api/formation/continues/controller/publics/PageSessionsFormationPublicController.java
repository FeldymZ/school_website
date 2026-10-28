package com.school.api.formation.continues.controller.publics;

import com.school.api.formation.continues.dto.PageSessionsFormationDTO;
import com.school.api.formation.continues.service.PageSessionsFormationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Site public — en-tête de la page « Sessions de formation »
 */
@RestController
@RequestMapping("/api/public/sessions-formation-page")
@RequiredArgsConstructor
public class PageSessionsFormationPublicController {

    private final PageSessionsFormationService service;

    @GetMapping
    public PageSessionsFormationDTO lire() {
        return service.lire();
    }
}