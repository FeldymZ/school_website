package com.school.api.formation.preinscription.entity;

public enum NiveauSouhaite {
    PREMIERE_ANNEE("1ère année"),
    DEUXIEME_ANNEE("2ème année"),
    TROISIEME_ANNEE("3ème année");

    private final String label;

    NiveauSouhaite(String label) { this.label = label; }

    public String getLabel() { return label; }
}