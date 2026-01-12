package com.school.api.actualite.dto;

public record ActualiteUpdateRequest(
  String title,
  String content,
  Integer displayOrder,
  Boolean enabled
) {}
