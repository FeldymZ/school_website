package com.school.api.formation.preinscription.entity;

public enum Civilite {
    M("M."),
    MME("Mme"),
    MLLE("Mlle");

    private final String label;

    Civilite(String label) { this.label = label; }

    public String getLabel() { return label; }
}