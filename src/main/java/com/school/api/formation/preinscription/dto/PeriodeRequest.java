package com.school.api.formation.preinscription.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PeriodeRequest(
        @NotNull Long sessionId,
        @NotNull Long emetteurId,
        @NotNull LocalDateTime dateDebut,
        @NotNull LocalDateTime dateFin
) {}