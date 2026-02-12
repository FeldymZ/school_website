package com.school.api.banner.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "banners")
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

  /*
   * displayOrder :
   * - utilisé uniquement pour les bannières actives
   * - null si bannière désactivée
   * - géré entièrement par le service
   */
  @Column(nullable = true)
  private Integer displayOrder;

  private Boolean enabled;

  private LocalDateTime startAt;
  private LocalDateTime endAt;

  // 🆕 BOUTON OPTIONNEL
  private String buttonLabel;
  private String buttonUrl;
}
