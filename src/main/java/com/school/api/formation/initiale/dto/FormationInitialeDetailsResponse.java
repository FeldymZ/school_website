package com.school.api.formation.initiale.dto;

import com.school.api.formation.initiale.entity.FormationInitialeLevel;
import lombok.Builder;

import java.util.List;

@Builder
public record FormationInitialeDetailsResponse(
  Long id,
  String title,
  String description,
  String coverImageUrl,
  List<String> galleryImages, // 👈 autres images ICI
  String pdfUrl,
  FormationInitialeLevel level
) {}
