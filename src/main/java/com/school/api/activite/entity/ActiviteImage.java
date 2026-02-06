package com.school.api.activite.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "activite_images")
public class ActiviteImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Lien vers l’activité
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "activite_id", nullable = false)
  private Activite activite;

  // URL publique du média (image ou vidéo)
  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ActiviteMediaType type;

  /* ================= GETTERS / SETTERS ================= */

  public Long getId() {
    return id;
  }

  public Activite getActivite() {
    return activite;
  }

  public void setActivite(Activite activite) {
    this.activite = activite;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public ActiviteMediaType getType() {
    return type;
  }

  public void setType(ActiviteMediaType type) {
    this.type = type;
  }
}
