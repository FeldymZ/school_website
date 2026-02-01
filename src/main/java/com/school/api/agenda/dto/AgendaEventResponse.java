package com.school.api.agenda.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record AgendaEventResponse(
  Long id,
  String title,
  String description,
  LocalDate eventDate,
  LocalDate endDate,
  LocalTime startTime,
  LocalTime endTime,
  String location,
  Boolean enabled // ✅ AJOUT OBLIGATOIRE
) {}
