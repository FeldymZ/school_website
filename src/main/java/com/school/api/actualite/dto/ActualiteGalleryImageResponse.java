package com.school.api.actualite.dto;

import lombok.Builder;

@Builder
public record ActualiteGalleryImageResponse(
  Long id,
  String url,
  Integer displayOrder
) {}
