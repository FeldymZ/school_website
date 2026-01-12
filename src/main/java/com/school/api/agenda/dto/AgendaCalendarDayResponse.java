package com.school.api.agenda.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record AgendaCalendarDayResponse(
  LocalDate date,
  List<AgendaCalendarEventResponse> events
) {}
