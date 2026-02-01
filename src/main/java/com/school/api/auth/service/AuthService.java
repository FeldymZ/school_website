package com.school.api.auth.service;

import com.school.api.auth.dto.LoginRequest;
import com.school.api.auth.dto.LoginResponse;
import com.school.api.auth.entity.User;
import com.school.api.auth.repository.UserRepository;
import com.school.api.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public LoginResponse login(LoginRequest request) {

    User user = userRepository.findByEmail(request.email())
      .orElseThrow(() -> new RuntimeException("Identifiants invalides"));

    if (!user.getEnabled()) {
      throw new RuntimeException("Compte désactivé");
    }

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new RuntimeException("Identifiants invalides");
    }

    String accessToken = jwtService.generateAccessToken(user);
    return new LoginResponse(accessToken);
  }
}
