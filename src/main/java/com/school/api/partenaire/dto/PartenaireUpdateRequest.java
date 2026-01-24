package com.school.api.partenaire.dto;

public record PartenaireUpdateRequest(
  String name,
  String websiteUrl,
  Integer displayOrder,
  Boolean enabled
) {}
