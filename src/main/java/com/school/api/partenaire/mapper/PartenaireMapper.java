package com.school.api.partenaire.mapper;

import com.school.api.partenaire.dto.PartenaireResponse;
import com.school.api.partenaire.entity.Partenaire;

public class PartenaireMapper {

  private PartenaireMapper() {}

  public static PartenaireResponse toResponse(Partenaire p) {
    return PartenaireResponse.builder()
      .id(p.getId())
      .name(p.getName())
      .logoUrl(p.getLogoUrl())
      .websiteUrl(p.getWebsiteUrl())
      .build();
  }
}
