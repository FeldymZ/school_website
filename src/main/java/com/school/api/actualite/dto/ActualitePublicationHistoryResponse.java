package com.school.api.actualite.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record ActualitePublicationHistoryResponse(
  LocalDateTime actionDate,
  String action
) {}
