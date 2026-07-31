package com.school.api.auth.controller;

import com.school.api.auth.dto.PhotoResponse;
import com.school.api.auth.dto.UserResponse;
import com.school.api.auth.entity.User;
import com.school.api.auth.repository.UserRepository;
import com.school.api.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class ProfileController {

  private final UserRepository userRepository;
  private final UserService userService;

  @GetMapping
  public UserResponse me(Authentication auth) {

    User user = userRepository.findByEmail(auth.getName())
            .orElseThrow();

    return UserResponse.builder()
            .id(user.getId())
            .nom(user.getNom())
            .prenom(user.getPrenom())
            .email(user.getEmail())
            .hasPhoto(user.getPhoto() != null && user.getPhoto().length > 0)
            .role(user.getRole().name())
            .enabled(user.getEnabled())
            .menuAccess(user.getMenuAccess())
            .build();
  }

  // 🆕 Photo de l'utilisateur connecté
  @GetMapping("/photo")
  public ResponseEntity<byte[]> myPhoto(Authentication auth) {
    PhotoResponse photo = userService.getMyPhoto(auth.getName());
    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(photo.contentType()))
            .body(photo.data());
  }

}