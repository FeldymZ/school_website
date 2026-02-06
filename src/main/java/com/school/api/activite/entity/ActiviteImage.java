package com.school.api.activite.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "activite_images")
public class ActiviteImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String fileUrl; // /files/...

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ActiviteMediaType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "activite_id", nullable = false)
  private Activite activite;

  /* GETTERS / SETTERS */

  public Long getId() { return id; }

  public String getFileUrl() { return fileUrl; }

  public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

  public ActiviteMediaType getType() { return type; }

  public void setType(ActiviteMediaType type) { this.type = type; }

  public Activite getActivite() { return activite; }

  public void setActivite(Activite activite) { this.activite = activite; }
}
