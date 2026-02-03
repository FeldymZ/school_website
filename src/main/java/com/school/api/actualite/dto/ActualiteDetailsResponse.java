package com.school.api.actualite.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ActualiteDetailsResponse(
  Long id,
  String title,
  String slug,
  String content,
  String coverImageUrl,

  // 🌍 PUBLIC (inchangé)
  List<String> galleryImages,

  // 🔐 ADMIN (NOUVEAU)
  List<ActualiteGalleryImageResponse> galleryImagesAdmin,

  Integer displayOrder,
  LocalDateTime publishedAt
) {}
