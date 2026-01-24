package com.school.api.partenaire.dto;

import lombok.Builder;

@Builder
public record PartenaireResponse(
  Long id,
  String name,
  String logoUrl,
  String websiteUrl
) {}
