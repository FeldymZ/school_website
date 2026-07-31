package com.school.api.auth.controller;

import com.school.api.auth.dto.PhotoResponse;
import com.school.api.auth.dto.UpdateUserInfoRequest;
import com.school.api.auth.dto.UserResponse;
import com.school.api.auth.entity.User;
import com.school.api.auth.repository.UserRepository;
import com.school.api.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

  @GetMapping("/photo")
  public ResponseEntity<byte[]> myPhoto(Authentication auth) {
    PhotoResponse photo = userService.getMyPhoto(auth.getName());
    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(photo.contentType()))
            .body(photo.data());
  }

  // 🆕 Modifier son propre profil (nom, prénom, email, photo)
  @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public UserResponse updateMe(
          @RequestPart("data") @Valid UpdateUserInfoRequest request,
          @RequestPart(value = "photo", required = false) MultipartFile photo,
          Authentication auth
  ) {
    return userService.updateMyInfo(auth.getName(), request, photo);
  }

}