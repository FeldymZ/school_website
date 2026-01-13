package com.school.api.banner.dto;

import java.time.LocalDateTime;

public record BannerUpdateRequest(
  String title,
  String subtitle,
  String subtitleAlt,
  Integer displayOrder,
  Boolean enabled,
  LocalDateTime startAt,
  LocalDateTime endAt,

  // 🆕 bouton optionnel
  String buttonLabel,
  String buttonUrl
) {}
