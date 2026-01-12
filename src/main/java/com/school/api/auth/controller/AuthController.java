package com.school.api.auth.controller;

import com.school.api.auth.dto.LoginRequest;
import com.school.api.auth.dto.LoginResponse;
import com.school.api.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
    @RequestBody @Valid LoginRequest request
  ) {
    return ResponseEntity.ok(authService.login(request));
  }
}
