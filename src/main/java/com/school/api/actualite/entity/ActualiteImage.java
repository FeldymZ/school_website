package com.school.api.actualite.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "actualite_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualiteImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Lien vers l’article
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "actualite_id", nullable = false)
  private Actualite actualite;

  // Image secondaire (galerie)
  @Column(nullable = false)
  private String imageUrl;

  // Ordre d’affichage dans la galerie
  private Integer displayOrder;
}
