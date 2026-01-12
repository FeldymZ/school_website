package com.school.api.actualite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "actualite_publication_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualitePublicationHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "actualite_id", nullable = false)
  private Actualite actualite;

  @Column(nullable = false)
  private LocalDateTime actionDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PublicationAction action;
}
