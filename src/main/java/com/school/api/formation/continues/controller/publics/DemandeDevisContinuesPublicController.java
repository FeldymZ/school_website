package com.school.api.formation.continues.controller.publics;

import com.school.api.formation.continues.dto.CreateDemandeDevisContinuesDTO;
import com.school.api.formation.continues.dto.CreateDemandeDevisGlobalDTO;
import com.school.api.formation.continues.service.DemandeDevisContinuesPublicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/demandes-devis")
@RequiredArgsConstructor
public class DemandeDevisContinuesPublicController {

    private final DemandeDevisContinuesPublicService service;

    @PostMapping
    public Map<String, Object> create(
            @RequestBody @Valid CreateDemandeDevisContinuesDTO dto
    ) {

        service.create(dto);

        return Map.of(
                "success", true,
                "message", "Demande envoyée avec succès"
        );
    }


    @PostMapping("/global")
    public Map<String, Object> createGlobal(
            @RequestBody @Valid CreateDemandeDevisGlobalDTO dto
    ) {

        service.createGlobal(dto);

        return Map.of(
                "success", true,
                "message", "Demande globale envoyée avec succès"
        );
    }
}