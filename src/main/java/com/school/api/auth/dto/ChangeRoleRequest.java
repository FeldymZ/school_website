package com.school.api.auth.dto;

import com.school.api.auth.entity.Role;
import jakarta.validation.constraints.NotNull;

public record ChangeRoleRequest(
  @NotNull Role role
) {}
