package com.school.api.actualite.controller;

import com.school.api.actualite.dto.ActualiteDetailsResponse;
import com.school.api.actualite.dto.ActualiteResponse;
import com.school.api.actualite.service.ActualiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/public/actualites")
@RequiredArgsConstructor
public class ActualitePublicController {

  private final ActualiteService service;

  /* =========================
     LISTE PUBLIQUE
     ========================= */
  @GetMapping
  public List<ActualiteResponse> list() {
    return service.getPublic();
  }

  /* =========================
     🔥 URL CANONIQUE (SLUG)
     ========================= */
  @GetMapping("/slug/{slug}")
  public ActualiteDetailsResponse detailsBySlug(
    @PathVariable String slug
  ) {
    return service.getDetailsBySlug(slug);
  }

  /* =========================
     🔁 REDIRECTION ID → SLUG
     ========================= */
  @GetMapping("/{id}")
  public ResponseEntity<Void> redirectById(
    @PathVariable Long id
  ) {

    String slug = service.getSlugById(id);

    URI target = URI.create(
      "/api/public/actualites/slug/" + slug
    );

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(target);

    return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
  }
}
