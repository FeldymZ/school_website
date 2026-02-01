
package com.school.api.auth.controller;

import com.school.api.auth.entity.AdminAuditLog;
import com.school.api.auth.repository.AdminAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/audit")
@RequiredArgsConstructor
public class AuditController {

  private final AdminAuditLogRepository auditRepository;

  /**
   * 📜 Tous les logs d’audit
   */
  @GetMapping
  public List<AdminAuditLog> all() {
    return auditRepository.findAll();
  }

  /**
   * 👤 Logs par administrateur (email)
   */
  @GetMapping("/by-admin")
  public List<AdminAuditLog> byAdmin(
    @RequestParam String email
  ) {
    return auditRepository.findByActorEmail(email);
  }

  /**
   * 📅 Logs par période
   */
  @GetMapping("/by-date")
  public List<AdminAuditLog> byDate(
    @RequestParam LocalDate start,
    @RequestParam LocalDate end
  ) {
    return auditRepository.findByCreatedAtBetween(
      start.atStartOfDay(),
      end.atTime(23, 59, 59)
    );
  }
}
