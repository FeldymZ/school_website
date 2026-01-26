package com.school.api.vieetudiante.visiteentreprise.dto;

import java.time.LocalDateTime;
import java.util.List;

public record VisiteEntrepriseResponse(
        Long id,
        String titre,
        String contenu,
        List<String> photos,
        LocalDateTime datePublication
) {
}
