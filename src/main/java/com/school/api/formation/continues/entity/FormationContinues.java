package com.school.api.formation.continues.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "formations_continues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormationContinues {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String titre;

  @Column(unique = true)
  private String slug;

  @Column(length = 3000)
  private String description;

  private String coverUrl;

  private String pdfUrl;

  @Column(nullable = false)
  private boolean enabled = true;

  @OneToMany(mappedBy = "formation")
  private List<DemandeDevisFormationContinues> demandes = new ArrayList<>();
}
