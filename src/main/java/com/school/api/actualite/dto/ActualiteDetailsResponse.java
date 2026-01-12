package com.school.api.actualite.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ActualiteDetailsResponse(
  Long id,
  String title,
  String content,
  String coverImageUrl,
  List<String> galleryImages,
  LocalDateTime publishedAt
) {}
