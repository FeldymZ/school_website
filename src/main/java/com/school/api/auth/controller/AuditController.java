package com.school.api.auth.controller;

import com.school.api.auth.dto.UserResponse;
import com.school.api.auth.entity.AuditLog;
import com.school.api.auth.repository.AuditLogRepository;
import com.school.api.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/audit")
@RequiredArgsConstructor
public class AuditController {

  private final AuditLogRepository auditRepository;

  @GetMapping
  public List<AuditLog> all() {
    return auditRepository.findAll();
  }

  @GetMapping("/by-admin")
  public List<AuditLog> byAdmin(@RequestParam String email) {
    return auditRepository.findByActorEmail(email);
  }

  @GetMapping("/by-date")
  public List<AuditLog> byDate(
    @RequestParam LocalDate start,
    @RequestParam LocalDate end
  ) {
    return auditRepository.findByCreatedAtBetween(
      start.atStartOfDay(),
      end.atTime(23, 59, 59)
    );
  }
}
