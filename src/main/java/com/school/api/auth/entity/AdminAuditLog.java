package com.school.api.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_audit_logs")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAuditLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;



  // Email de l’admin qui agit
  @Column(nullable = false)
  private String actorEmail;

  // Action réalisée
  @Column(nullable = false)
  private String action;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String target;

  // Date
  @Column(nullable = false)
  private LocalDateTime createdAt;
}
