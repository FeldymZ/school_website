package com.school.api.partenaire.controller;

import com.school.api.partenaire.entity.Partenaire;
import com.school.api.partenaire.service.PartenaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/partenaires")
@RequiredArgsConstructor
public class PartenairePublicController {

  private final PartenaireService service;

  @GetMapping
  public List<Partenaire> list() {
    return service.getPublic();
  }
}
