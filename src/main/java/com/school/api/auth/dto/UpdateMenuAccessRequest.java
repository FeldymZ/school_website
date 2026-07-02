package com.school.api.auth.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UpdateMenuAccessRequest(
        @NotNull Set<String> menuAccess
) {}