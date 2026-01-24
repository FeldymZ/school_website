package com.school.api.formation.initiale.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "formations_initiales")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormationInitiale {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Nom réel (ex: Cyber Défense)
  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  // 🟢 IMAGE PRINCIPALE (portrait)
  @Column(nullable = false)
  private String coverImageUrl;

  private String pdfUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FormationInitialeLevel level; // LICENCE / MASTER

  private Integer displayOrder;

  private Boolean enabled;
}
