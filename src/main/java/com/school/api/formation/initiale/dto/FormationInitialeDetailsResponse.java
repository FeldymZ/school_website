package com.school.api.formation.initiale.dto;

import com.school.api.formation.initiale.entity.FormationInitialeLevel;
import lombok.Builder;

import java.util.List;

@Builder
public record FormationInitialeDetailsResponse(
  Long id,
  String title,
  String slug,
  String description,
  String coverImageUrl,
  List<FormationGalleryImageResponse> galleryImages,
  String pdfUrl,
  FormationInitialeLevel level
) {}
