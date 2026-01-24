package com.school.api.formation.preinscription.dto;

import jakarta.validation.constraints.NotNull;

public record PreinscriptionDecisionRequest(
  @NotNull Boolean accepted,
  String commentaire
) {}
