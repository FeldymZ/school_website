package com.school.api.activite.dto;

import java.util.List;

public class ActiviteResponse {

  private Long id;
  private String titre;
  private String contenu;
  private String slug;
  private List<ActiviteMediaResponse> medias;

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public String getTitre() { return titre; }

  public void setTitre(String titre) { this.titre = titre; }

  public String getContenu() { return contenu; }

  public void setContenu(String contenu) { this.contenu = contenu; }

  public String getSlug() { return slug; }

  public void setSlug(String slug) { this.slug = slug; }

  public List<ActiviteMediaResponse> getMedias() { return medias; }

  public void setMedias(List<ActiviteMediaResponse> medias) {
    this.medias = medias;
  }
}
