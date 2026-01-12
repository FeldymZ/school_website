package com.school.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAdminRequest(

  @Email
  @NotBlank
  String email,

  @NotBlank
  @Size(min = 8)
  String password

) {}
