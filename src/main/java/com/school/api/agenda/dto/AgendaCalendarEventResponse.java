package com.school.api.agenda.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record AgendaCalendarEventResponse(
  Long id,
  String title,
  String description,
  LocalDate startDate,
  LocalDate endDate,
  LocalTime startTime,
  LocalTime endTime,
  String location,
  boolean multiDay
) {}
