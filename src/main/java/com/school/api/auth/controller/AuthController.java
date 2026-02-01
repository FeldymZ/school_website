package com.school.api.auth.controller;

import com.school.api.auth.dto.LoginRequest;
import com.school.api.auth.dto.LoginResponse;
import com.school.api.auth.entity.User;
import com.school.api.auth.repository.UserRepository;
import com.school.api.auth.security.JwtService;
import com.school.api.auth.service.AuthService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final JwtService jwtService;
  private final UserRepository userRepository;

  /* ================= LOGIN ================= */
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
    @RequestBody @Valid LoginRequest request,
    HttpServletResponse response
  ) {
    LoginResponse loginResponse = authService.login(request);

    User user = userRepository.findByEmail(request.email())
      .orElseThrow();

    String refreshToken = jwtService.generateRefreshToken(user);

    setRefreshCookie(response, refreshToken);

    return ResponseEntity.ok(loginResponse);
  }

  /* ================= REFRESH ================= */
  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refresh(
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    String refreshToken = extractRefreshToken(request);

    if (refreshToken == null) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    try {
      Claims claims = jwtService.parse(refreshToken);
      String email = claims.getSubject();

      User user = userRepository.findByEmail(email)
        .orElseThrow();

      String newAccessToken = jwtService.generateAccessToken(user);
      String newRefreshToken = jwtService.generateRefreshToken(user);

      setRefreshCookie(response, newRefreshToken);

      return ResponseEntity.ok(
        new LoginResponse(newAccessToken)
      );
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  /* ================= LOGOUT ================= */
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletResponse response) {
    clearRefreshCookie(response);
    return ResponseEntity.noContent().build();
  }

  /* ================= COOKIE HELPERS ================= */

  private void setRefreshCookie(HttpServletResponse response, String token) {
    response.setHeader(
      "Set-Cookie",
      "refreshToken=" + token +
      "; HttpOnly; Path=/api/auth/refresh; Max-Age=604800; SameSite=Strict"
    );
  }

  private void clearRefreshCookie(HttpServletResponse response) {
    response.setHeader(
      "Set-Cookie",
      "refreshToken=; HttpOnly; Path=/api/auth/refresh; Max-Age=0; SameSite=Strict"
    );
  }

  private String extractRefreshToken(HttpServletRequest request) {
    if (request.getCookies() == null) return null;

    for (var cookie : request.getCookies()) {
      if ("refreshToken".equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    return null;
  }
}
