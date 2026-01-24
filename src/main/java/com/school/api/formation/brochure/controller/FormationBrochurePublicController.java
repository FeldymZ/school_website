package com.school.api.formation.brochure.controller;

import com.school.api.formation.brochure.dto.FormationBrochureRequestDto;
import com.school.api.formation.brochure.service.FormationBrochureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/formations/initiale/{formationId}/brochure")
@RequiredArgsConstructor
public class FormationBrochurePublicController {

  private final FormationBrochureService service;

  @PostMapping
  public void requestBrochure(
    @PathVariable Long formationId,
    @RequestBody @Valid FormationBrochureRequestDto request
  ) {
    service.sendBrochure(formationId, request);
  }
}
