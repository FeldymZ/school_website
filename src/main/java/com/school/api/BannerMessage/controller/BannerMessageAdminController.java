package com.school.api.BannerMessage.controller;

import com.school.api.BannerMessage.entity.BannerMessage;
import com.school.api.BannerMessage.service.BannerMessageService;
import com.school.api.auth.audit.AuditLog;
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

  @AuditLog(action = "CONSULTATION_BANNER_MESSAGES")
  @GetMapping
  public List<BannerMessage> list() {
    return service.getAll();
  }

  @AuditLog(action = "CREATION_BANNER_MESSAGE", target = "#content", failureAction = "CREATION_BANNER_MESSAGE_ECHEC")
  @PostMapping(consumes = "multipart/form-data")
  public BannerMessage create(
          @RequestParam(required = false) String title,
          @RequestParam String content,
          @RequestParam(defaultValue = "false") boolean active
  ) {
    return service.create(title, content, active);
  }

  @AuditLog(action = "MODIFICATION_BANNER_MESSAGE", target = "#id.toString()", failureAction = "MODIFICATION_BANNER_MESSAGE_ECHEC")
  @PutMapping(value = "/{id}", consumes = "multipart/form-data")
  public BannerMessage update(
          @PathVariable Long id,
          @RequestParam(required = false) String title,
          @RequestParam(required = false) String content,
          @RequestParam(required = false) Boolean active
  ) {
    return service.updatePartial(id, title, content, active);
  }

  @AuditLog(action = "TOGGLE_BANNER_MESSAGE", target = "#id.toString()")
  @PutMapping("/{id}/active")
  public BannerMessage toggleActive(
          @PathVariable Long id,
          @RequestParam boolean active
  ) {
    return service.setActive(id, active);
  }

  @AuditLog(action = "SUPPRESSION_BANNER_MESSAGE", target = "#id.toString()", failureAction = "SUPPRESSION_BANNER_MESSAGE_ECHEC")
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}