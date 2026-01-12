package com.school.api.agenda.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record AgendaCalendarResponse(
  int year,
  int month,
  List<AgendaCalendarDayResponse> days
) {}
