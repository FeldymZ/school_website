package com.school.api.actualite.controller;

import com.school.api.actualite.dto.*;
import com.school.api.actualite.service.ActualiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/actualites")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class ActualiteAdminController {

  private final ActualiteService service;

  @GetMapping
  public List<ActualiteResponse> all() {
    return service.getAll();
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ActualiteResponse create(
    @RequestParam String title,
    @RequestParam String content,
    @RequestParam Integer displayOrder,
    @RequestParam(required = false) Boolean enabled,
    @RequestParam MultipartFile coverImage
  ) {
    return service.create(title, content, displayOrder, enabled, coverImage);
  }

  @PutMapping("/{id}")
  public ActualiteResponse update(
    @PathVariable Long id,
    @RequestBody ActualiteUpdateRequest request
  ) {
    return service.update(id, request);
  }

  @PutMapping(
    value = "/{id}/cover",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ActualiteResponse updateCover(
    @PathVariable Long id,
    @RequestParam MultipartFile coverImage
  ) {
    return service.updateCover(id, coverImage);
  }

  @PostMapping(
    value = "/{id}/images",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public void addImages(
    @PathVariable Long id,
    @RequestParam("images") List<MultipartFile> images
  ) {
    service.addGalleryImages(id, images);
  }

  @PutMapping(
    value = "/{id}/images",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public void replaceImages(
    @PathVariable Long id,
    @RequestParam("images") List<MultipartFile> images
  ) {
    service.replaceGalleryImages(id, images);
  }

  @GetMapping("/{id}/history")
  public List<ActualitePublicationHistoryResponse> history(
    @PathVariable Long id
  ) {
    return service.getPublicationHistory(id);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @PutMapping("/reorder")
  public void reorder(@RequestBody ActualiteReorderRequest request) {
    service.reorder(request);
  }

}
