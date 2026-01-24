package com.school.api.actualite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "actualites")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Actualite {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  // Cover image
  @Column(nullable = false)
  private String coverImageUrl;

  private Integer displayOrder;

  // Visibilité
  @Column(nullable = false)
  private Boolean enabled;

  // 📅 Date + heure de publication
  private LocalDateTime publishedAt;
}
