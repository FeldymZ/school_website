package com.school.api.stat.keyfigure.controller;

import com.school.api.stat.keyfigure.dto.KeyFigureResponse;
import com.school.api.stat.keyfigure.service.KeyFigureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/key-figures")
@RequiredArgsConstructor
public class KeyFigurePublicController {

  private final KeyFigureService service;

  @GetMapping
  public List<KeyFigureResponse> get() {
    return service.getPublic();
  }
}
