package com.school.api.auth.repository;

import com.school.api.auth.entity.AdminAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminAuditLogRepository
  extends JpaRepository<AdminAuditLog, Long> {

  /**
   * 👤 Logs par email d’admin
   */
  List<AdminAuditLog> findByActorEmail(String actorEmail);

  /**
   * 📅 Logs par période
   */
  List<AdminAuditLog> findByCreatedAtBetween(
    LocalDateTime start,
    LocalDateTime end
  );
}
