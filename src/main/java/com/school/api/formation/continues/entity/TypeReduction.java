package com.school.api.formation.continues.entity;

/** Mode de réduction appliqué au devis */
public enum TypeReduction {
    AUCUNE,   // pas de réduction
    TAUX,     // pourcentage du total (ex : 10 %)
    MONTANT   // somme fixe en XAF (ex : 50 000)
}