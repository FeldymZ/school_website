package com.school.api.activite.controller;

import com.school.api.activite.dto.ActivitePublicResponse;
import com.school.api.activite.service.ActiviteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/activites")
public class ActivitePublicController {

  private final ActiviteService activiteService;

  public ActivitePublicController(ActiviteService activiteService) {
    this.activiteService = activiteService;
  }

  @GetMapping
  public List<ActivitePublicResponse> getAll() {
    return activiteService.getAllPublic();
  }

  @GetMapping("/{id}")
  public ActivitePublicResponse getById(@PathVariable Long id) {
    return activiteService.getPublicById(id);
  }
}
