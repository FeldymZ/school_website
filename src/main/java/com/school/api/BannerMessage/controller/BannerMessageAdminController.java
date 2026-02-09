package com.school.api.BannerMessage.controller;

import com.school.api.BannerMessage.entity.BannerMessage;
import com.school.api.BannerMessage.service.BannerMessageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/banner-message")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
public class BannerMessageAdminController {

  private final BannerMessageService service;

  public BannerMessageAdminController(BannerMessageService service) {
    this.service = service;
  }

  @GetMapping
  public List<BannerMessage> list() {
    return service.getAll();
  }

  @PostMapping(consumes = "multipart/form-data")
  public BannerMessage create(
    @RequestParam(required = false) String title,
    @RequestParam String content,
    @RequestParam(defaultValue = "false") boolean active
  ) {
    return service.create(title, content, active);
  }

  @PutMapping(value = "/{id}", consumes = "multipart/form-data")
  public BannerMessage update(
    @PathVariable Long id,
    @RequestParam(required = false) String title,
    @RequestParam String content,
    @RequestParam(defaultValue = "false") boolean active
  ) {
    return service.update(id, title, content, active);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
