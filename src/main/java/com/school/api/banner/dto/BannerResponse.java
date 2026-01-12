package com.school.api.banner.dto;

import com.school.api.banner.entity.BannerStatus;
import com.school.api.banner.entity.MediaType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BannerResponse(
  Long id,
  String title,
  String subtitle,
  String subtitleAlt,
  String mediaUrl,
  MediaType mediaType,
  Integer displayOrder,
  Boolean enabled,
  LocalDateTime startAt,
  LocalDateTime endAt,

  // 🆕 STATUT CALCULÉ
  BannerStatus status
) {}
