package com.school.api.formation.brochure.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FormationBrochureRequestDto(

  @NotBlank
  String name,

  @NotBlank
  @Email
  String email
) {}
