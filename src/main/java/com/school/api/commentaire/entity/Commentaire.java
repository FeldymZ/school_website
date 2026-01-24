package com.school.api.commentaire.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
  name = "commentaires",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = "displayOrder")
  }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Commentaire {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String authorName;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  // Date affichée manuellement
  @Column(nullable = false)
  private String displayDate;

  // Image de l’auteur
  @Column(nullable = false)
  private String authorImageUrl;

  // ❗ ORDRE UNIQUE
  @Column(nullable = false)
  private Integer displayOrder;

  @Column(nullable = false)
  private Boolean enabled;
}
