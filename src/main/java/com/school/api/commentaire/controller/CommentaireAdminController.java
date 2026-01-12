package com.school.api.commentaire.controller;

import com.school.api.commentaire.dto.*;
import com.school.api.commentaire.service.CommentaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/commentaires")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class CommentaireAdminController {

  private final CommentaireService service;

  @GetMapping
  public List<CommentaireResponse> all() {
    return service.getAll();
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public CommentaireResponse create(
    @RequestPart CommentaireCreateRequest request,
    @RequestPart MultipartFile authorImage
  ) {
    return service.create(request, authorImage);
  }

  @PutMapping("/{id}")
  public CommentaireResponse update(
    @PathVariable Long id,
    @RequestBody CommentaireUpdateRequest request
  ) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @PutMapping("/reorder")
  public void reorder(@RequestBody CommentaireReorderRequest request) {
    service.reorder(request);
  }

}
