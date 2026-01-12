package com.school.api.stat.keyfigure.dto;

public record KeyFigureRequest(

  String label,        // ex: "Étudiants"
  String value,        // ex: "2500+"
  Integer displayOrder,
  Boolean enabled

) {}
