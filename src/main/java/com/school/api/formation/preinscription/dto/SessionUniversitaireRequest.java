package com.school.api.formation.preinscription.dto;

import jakarta.validation.constraints.NotBlank;

public record SessionUniversitaireRequest(
        @NotBlank String annee
) {}