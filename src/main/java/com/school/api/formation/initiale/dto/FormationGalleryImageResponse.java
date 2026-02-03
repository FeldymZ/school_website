package com.school.api.formation.initiale.dto;

import lombok.Builder;

@Builder
public record FormationGalleryImageResponse(
  Long id,
  String imageUrl,
  Integer displayOrder
) {}
