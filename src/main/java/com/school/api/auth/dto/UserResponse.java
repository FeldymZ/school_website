package com.school.api.auth.dto;

import lombok.Builder;

@Builder
public record UserResponse(
  Long id,
  String email,
  String role,
  boolean enabled
) {}
