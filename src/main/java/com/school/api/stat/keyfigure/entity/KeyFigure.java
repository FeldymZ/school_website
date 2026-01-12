package com.school.api.stat.keyfigure.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "key_figures")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyFigure {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Texte affiché (ex: "Étudiants")
  @Column(nullable = false)
  private String label;

  // Valeur affichée (ex: "2500+", "95%")
  @Column(nullable = false)
  private String value;

  // Ordre d'affichage
  @Column(nullable = false)
  private Integer displayOrder;

  // Actif / inactif
  @Column(nullable = false)
  private Boolean enabled;
}
