package com.school.api.activite.dto;

import com.school.api.activite.entity.ActiviteMediaType;

public class ActiviteMediaResponse {

  private Long id;
  private String url;
  private ActiviteMediaType type;

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public String getUrl() { return url; }

  public void setUrl(String url) { this.url = url; }

  public ActiviteMediaType getType() { return type; }

  public void setType(ActiviteMediaType type) {
    this.type = type;
  }
}
