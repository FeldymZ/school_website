package com.school.api.auth.repository;

import com.school.api.auth.entity.AdminAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminAuditLogRepository
  extends JpaRepository<AdminAuditLog, Long> {
}
