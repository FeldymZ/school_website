package com.school.api.formation.preinscription.dto;

import jakarta.validation.constraints.NotBlank;

public record EmetteurRequest(
        @NotBlank String nom,
        @NotBlank String fonction
) {}