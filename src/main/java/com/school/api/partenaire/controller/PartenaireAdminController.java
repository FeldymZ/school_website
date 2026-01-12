package com.school.api.partenaire.controller;

import com.school.api.partenaire.entity.Partenaire;
import com.school.api.partenaire.service.PartenaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/partenaires")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class PartenaireAdminController {

  private final PartenaireService service;

  @GetMapping
  public List<Partenaire> all() {
    return service.getAll();
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Partenaire create(
    @RequestParam String name,
    @RequestParam(required = false) String websiteUrl,
    @RequestParam Integer displayOrder,
    @RequestParam(required = false) Boolean enabled,
    @RequestParam MultipartFile logo
  ) {
    return service.create(
      name,
      websiteUrl,
      displayOrder,
      enabled,
      logo
    );
  }

  @PutMapping("/{id}")
  public Partenaire update(
    @PathVariable Long id,
    @RequestParam(required = false) String name,
    @RequestParam(required = false) String websiteUrl,
    @RequestParam(required = false) Integer displayOrder,
    @RequestParam(required = false) Boolean enabled
  ) {
    return service.update(
      id,
      name,
      websiteUrl,
      displayOrder,
      enabled
    );
  }

  @PutMapping(
    value = "/{id}/logo",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public Partenaire updateLogo(
    @PathVariable Long id,
    @RequestParam MultipartFile logo
  ) {
    return service.updateLogo(id, logo);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @PutMapping(
    value = "/reorder",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public void reorder(
    @RequestParam("orderedIds") List<Long> orderedIds
  ) {
    service.reorder(orderedIds);
  }

}
