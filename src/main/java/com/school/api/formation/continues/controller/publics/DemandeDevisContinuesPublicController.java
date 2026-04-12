package com.school.api.formation.continues.controller.publics;

import com.school.api.formation.continues.dto.CreateDemandeDevisContinuesDTO;
import com.school.api.formation.continues.service.DemandeDevisContinuesPublicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/demandes-devis")
@RequiredArgsConstructor
public class DemandeDevisContinuesPublicController {

    private final DemandeDevisContinuesPublicService service;

    @PostMapping
    public String create(
            @RequestBody @Valid CreateDemandeDevisContinuesDTO dto
    ) {

        /* 🔥 VALIDATION ENTREPRISE */
        if (dto.isEntreprise() && (dto.getNomStructure() == null || dto.getNomStructure().isBlank())) {
            throw new RuntimeException("Le nom de la structure est obligatoire pour une entreprise");
        }

        service.create(dto);

        return "Demande envoyée";
    }
}