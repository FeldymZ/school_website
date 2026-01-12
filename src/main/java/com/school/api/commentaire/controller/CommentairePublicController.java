package com.school.api.commentaire.controller;

import com.school.api.commentaire.dto.CommentaireResponse;
import com.school.api.commentaire.service.CommentaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/commentaires")
@RequiredArgsConstructor
public class CommentairePublicController {

  private final CommentaireService service;

  @GetMapping
  public List<CommentaireResponse> list() {
    return service.getPublic();
  }
}
