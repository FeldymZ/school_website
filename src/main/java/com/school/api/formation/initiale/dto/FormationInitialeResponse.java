package com.school.api.formation.initiale.dto;

import com.school.api.formation.initiale.entity.FormationInitialeLevel;
import lombok.Builder;

@Builder
public record FormationInitialeResponse(
  Long id,
  String title,
  String slug,
  String coverImageUrl,
  FormationInitialeLevel level,
  Boolean enabled
) {}
