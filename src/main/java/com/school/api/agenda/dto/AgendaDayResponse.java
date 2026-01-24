package com.school.api.agenda.dto;

import lombok.Builder;

@Builder
public record AgendaDayResponse(
  int day,
  int count
) {}
