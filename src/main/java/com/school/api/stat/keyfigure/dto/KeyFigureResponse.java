package com.school.api.stat.keyfigure.dto;

import lombok.Builder;

@Builder
public record KeyFigureResponse(

  Long id,
  String label,
  String value,
  Integer displayOrder,
  Boolean enabled

) {}
