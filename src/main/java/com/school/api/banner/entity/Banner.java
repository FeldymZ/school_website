package com.school.api.banner.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
  name = "banners",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = "displayOrder")
  }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Banner {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private String subtitle;
  private String subtitleAlt;

  private String mediaUrl;

  @Enumerated(EnumType.STRING)
  private MediaType mediaType;

  @Column(nullable = false, unique = true)
  private Integer displayOrder;

  private Boolean enabled;

  private LocalDateTime startAt;
  private LocalDateTime endAt;

  // 🆕 BOUTON OPTIONNEL
  private String buttonLabel;
  private String buttonUrl;
}
