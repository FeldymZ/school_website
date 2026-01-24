package com.school.api.formation.initiale.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "formation_initiale_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormationInitialeImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "formation_id", nullable = false)
  private FormationInitiale formation;

  // Image secondaire (JAMAIS la cover)
  @Column(nullable = false)
  private String imageUrl;

  private Integer displayOrder;
}
