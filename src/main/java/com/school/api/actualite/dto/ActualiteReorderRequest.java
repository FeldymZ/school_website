package com.school.api.actualite.dto;

import java.util.List;

public record ActualiteReorderRequest(
  List<Long> orderedIds
) {}
