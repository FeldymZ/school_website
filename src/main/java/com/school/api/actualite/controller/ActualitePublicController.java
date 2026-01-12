package com.school.api.actualite.controller;

import com.school.api.actualite.dto.ActualiteDetailsResponse;
import com.school.api.actualite.dto.ActualiteResponse;
import com.school.api.actualite.service.ActualiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/actualites")
@RequiredArgsConstructor
public class ActualitePublicController {

  private final ActualiteService service;

  @GetMapping
  public List<ActualiteResponse> list() {
    return service.getPublic();
  }

  @GetMapping("/{id}")
  public ActualiteDetailsResponse details(@PathVariable Long id) {
    return service.getDetails(id);
  }
}
