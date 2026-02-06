package com.school.api.activite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activite {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 150)
  private String titre;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String contenu;

  @OneToMany(
    mappedBy = "activite",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  @Builder.Default
  private List<ActiviteImage> images = new ArrayList<>();
}
