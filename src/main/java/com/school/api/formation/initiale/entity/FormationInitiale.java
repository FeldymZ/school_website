package com.school.api.formation.initiale.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
  name = "formations_initiales",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = "slug")
  }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormationInitiale {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String slug; // ✅ NOUVEAU

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private String coverImageUrl;

  private String pdfUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FormationInitialeLevel level;

  private Integer displayOrder;

  private Boolean enabled;
}
