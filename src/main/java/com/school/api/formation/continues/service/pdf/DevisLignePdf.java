package com.school.api.formation.continues.service.pdf;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Une ligne du tableau du PDF (valeurs déjà formatées). Jasper lit les getters. */
@Getter
@AllArgsConstructor
public class DevisLignePdf {
    private String libelle;
    private String participants;
    private String prixUnitaire;
    private String montant;
}