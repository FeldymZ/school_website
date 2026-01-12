package com.school.api.banner.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record BannerRequest(

  @NotBlank
  String title,

  String subtitle,

  String subtitleAlt,

  Integer displayOrder,

  Boolean enabled,

  // 🆕
  LocalDateTime startAt,
  LocalDateTime endAt
) {}
