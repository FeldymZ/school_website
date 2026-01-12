package com.school.api.partenaire.dto;

public record PartenaireCreateRequest(
  String name,
  String websiteUrl,
  Integer displayOrder,
  Boolean enabled
) {}
