package com.school.api.activite.dto;

import java.util.List;

public class ActivitePublicResponse {

  private Long id;
  private String titre;
  private String contenu;
  private String slug;
  private List<ActiviteMediaPublicResponse> medias;

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public String getTitre() { return titre; }

  public void setTitre(String titre) { this.titre = titre; }

  public String getContenu() { return contenu; }

  public void setContenu(String contenu) { this.contenu = contenu; }

  public String getSlug() { return slug; }

  public void setSlug(String slug) { this.slug = slug; }

  public List<ActiviteMediaPublicResponse> getMedias() {
    return medias;
  }

  public void setMedias(List<ActiviteMediaPublicResponse> medias) {
    this.medias = medias;
  }
}
