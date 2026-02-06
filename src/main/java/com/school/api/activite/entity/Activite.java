package com.school.api.activite.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activites")
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
  private List<ActiviteImage> images = new ArrayList<>();

  /* GETTERS / SETTERS */

  public Long getId() { return id; }

  public String getTitre() { return titre; }

  public void setTitre(String titre) { this.titre = titre; }

  public String getContenu() { return contenu; }

  public void setContenu(String contenu) { this.contenu = contenu; }

  public List<ActiviteImage> getImages() { return images; }
}
