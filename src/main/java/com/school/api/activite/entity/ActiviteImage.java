package com.school.api.activite.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "activite_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiviteImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String fileName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "activite_id", nullable = false)
  private Activite activite;
}
