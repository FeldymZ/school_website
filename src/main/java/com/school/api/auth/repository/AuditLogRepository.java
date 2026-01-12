package com.school.api.auth.repository;

import com.school.api.auth.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

  List<AuditLog> findByActorEmail(String actorEmail);

  List<AuditLog> findByCreatedAtBetween(
    LocalDateTime start,
    LocalDateTime end
  );
}
