package com.school.api.auth.dto;

/** Création / modification d'un module (les champs vides sont ignorés ou mis à null) */
public record MenuModuleRequest(
        String cle,
        String libelle,
        String chemin,
        String section,
        String groupe,
        String icone,
        Boolean actif,
        Boolean superAdminSeulement
) {
}