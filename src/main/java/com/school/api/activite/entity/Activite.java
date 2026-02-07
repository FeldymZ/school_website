package com.school.api.activite.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
  name = "activites",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = "slug")
  }
)
public class Activite {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 150)
  private String titre;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String contenu;

  @Column(nullable = false, length = 180, unique = true)
  private String slug;

  @OneToMany(
    mappedBy = "activite",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<ActiviteImage> images = new ArrayList<>();

  /* ================= GETTERS / SETTERS ================= */

  public Long getId() { return id; }

  public String getTitre() { return titre; }

  public void setTitre(String titre) { this.titre = titre; }

  public String getContenu() { return contenu; }

  public void setContenu(String contenu) { this.contenu = contenu; }

  public String getSlug() { return slug; }

  public void setSlug(String slug) { this.slug = slug; }

  public List<ActiviteImage> getImages() { return images; }
}
