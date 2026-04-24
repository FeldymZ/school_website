package com.school.api.formation.preinscription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SessionRequest(
        @NotBlank String anneeUniversitaire,
        @NotNull LocalDateTime dateDebut,
        @NotNull LocalDateTime dateFin,
        @NotNull Long emetteurId
) {}