package com.school.api.actualite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
  name = "actualites",
  uniqueConstraints = @UniqueConstraint(columnNames = "slug")
)
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

  @Column(nullable = false, unique = true)
  private String slug;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(nullable = false)
  private String coverImageUrl;

  private Integer displayOrder;

  @Column(nullable = false)
  private Boolean enabled;

  private LocalDateTime publishedAt;
}
