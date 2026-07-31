package com.school.api.auth.controller;

import com.school.api.auth.dto.PhotoResponse;
import com.school.api.auth.dto.UserResponse;
import com.school.api.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints "moi-même" — accessibles à tout utilisateur authentifié,
 * sans nécessiter la permission ADMINISTRATION_UTILISATEURS.
 * Permet à un ADMIN de consulter son propre profil / sa propre photo,
 * même s'il n'a pas le droit de gérer les autres utilisateurs.
 */
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final UserService userService;

    @GetMapping
    public UserResponse me(Authentication auth) {
        return userService.getMe(auth.getName());
    }

    @GetMapping("/photo")
    public ResponseEntity<byte[]> myPhoto(Authentication auth) {
        PhotoResponse photo = userService.getMyPhoto(auth.getName());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.contentType()))
                .body(photo.data());
    }
}