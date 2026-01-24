package com.school.api.formation.brochure.entity;

import com.school.api.formation.initiale.entity.FormationInitiale;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "formation_brochure_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormationBrochureRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String studentName;

  @Column(nullable = false)
  private String email;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "formation_id", nullable = false)
  private FormationInitiale formation;

  @Column(nullable = false)
  private LocalDateTime requestedAt;
}
