package com.school.api.agenda.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendaEventUpdateRequest(
  String title,
  String description,
  LocalDate eventDate,
  LocalDate endDate,
  LocalTime startTime,
  LocalTime endTime,
  String location,

  Boolean enabled
) {}
