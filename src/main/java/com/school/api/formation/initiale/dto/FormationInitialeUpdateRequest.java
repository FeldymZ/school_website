package com.school.api.formation.initiale.dto;

import com.school.api.formation.initiale.entity.FormationInitialeLevel;

public record FormationInitialeUpdateRequest(
  String name,
  String description,
  FormationInitialeLevel level,
  Integer displayOrder,
  Boolean enabled
) {}
