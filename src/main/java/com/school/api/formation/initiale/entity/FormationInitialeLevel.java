package com.school.api.formation.initiale.entity;

public enum FormationInitialeLevel {

  LICENCE("Licence"),
  MASTER("Master");

  private final String label;

  FormationInitialeLevel(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
