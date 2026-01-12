package com.school.api.auth.service;

import com.school.api.auth.entity.AdminAuditLog;
import com.school.api.auth.repository.AdminAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminAuditService {

  private final AdminAuditLogRepository repository;

  public void log(String actorEmail, String action, String target) {

    repository.save(
      AdminAuditLog.builder()
        .actorEmail(actorEmail)
        .action(action)
        .target(target)
        .createdAt(LocalDateTime.now())
        .build()
    );
  }


}
