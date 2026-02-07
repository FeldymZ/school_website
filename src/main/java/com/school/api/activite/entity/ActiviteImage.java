package com.school.api.activite.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "activite_images")
public class ActiviteImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "activite_id", nullable = false)
  private Activite activite;

  @Column(name = "image_url", nullable = false)
  private String imageUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ActiviteMediaType type;

  public Long getId() { return id; }

  public Activite getActivite() { return activite; }

  public void setActivite(Activite activite) {
    this.activite = activite;
  }

  public String getImageUrl() { return imageUrl; }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public ActiviteMediaType getType() { return type; }

  public void setType(ActiviteMediaType type) {
    this.type = type;
  }
}
