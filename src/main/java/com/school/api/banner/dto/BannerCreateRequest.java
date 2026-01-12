package com.school.api.banner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BannerCreateRequest(

  @NotBlank
  String title,

  String subtitle,

  String subtitleAlt,

  @NotNull
  Integer displayOrder,

  Boolean enabled,

  // 🆕 OPTIONNELLES
  LocalDateTime startAt,
  LocalDateTime endAt
) {}
