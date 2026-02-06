package com.school.api.activite.dto;

import jakarta.validation.constraints.NotBlank;

public class ActiviteRequest {

  @NotBlank(message = "Le titre est obligatoire")
  private String titre;

  @NotBlank(message = "Le contenu est obligatoire")
  private String contenu;

  /* ================= GETTERS / SETTERS ================= */

  public String getTitre() {
    return titre;
  }

  public void setTitre(String titre) {
    this.titre = titre;
  }

  public String getContenu() {
    return contenu;
  }

  public void setContenu(String contenu) {
    this.contenu = contenu;
  }
}
