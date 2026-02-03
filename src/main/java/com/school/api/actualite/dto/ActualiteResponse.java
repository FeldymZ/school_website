package com.school.api.actualite.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record ActualiteResponse(
  Long id,
  String title,
  String slug,
  String coverImageUrl,
  LocalDateTime publishedAt
) {}
