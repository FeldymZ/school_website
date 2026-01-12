package com.school.api.auth.controller;

import com.school.api.auth.dto.UserResponse;
import com.school.api.auth.entity.User;
import com.school.api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class ProfileController {

  private final UserRepository userRepository;

  @GetMapping
  public UserResponse me(Authentication auth) {

    User user = userRepository.findByEmail(auth.getName())
      .orElseThrow();

    return UserResponse.builder()
      .id(user.getId())
      .email(user.getEmail())
      .role(user.getRole().name()) // ✅ conversion enum → String
      .enabled(user.getEnabled())
      .build();
  }

}
