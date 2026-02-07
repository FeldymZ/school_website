package com.school.api.activite.dto;

import com.school.api.activite.entity.ActiviteMediaType;

public class ActiviteMediaPublicResponse {

  private String url;
  private ActiviteMediaType type;

  public String getUrl() { return url; }

  public void setUrl(String url) { this.url = url; }

  public ActiviteMediaType getType() { return type; }

  public void setType(ActiviteMediaType type) {
    this.type = type;
  }
}
