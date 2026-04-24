package com.school.api.auth.service;

import com.school.api.auth.entity.AdminAuditLog;
import com.school.api.auth.repository.AdminAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AdminAuditService {

  private final AdminAuditLogRepository repository;

  public void log(String actorEmail, String action, String target) {

    LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Paris"));

    repository.save(
            AdminAuditLog.builder()
                    .actorEmail(actorEmail)
                    .action(action)
                    .target(target)
                    .createdAt(now)
                    .build()
    );
  }

}