package com.school.api.auth.entity;

import java.util.HashSet;
import java.util.Set;

public enum MenuPermission {

    // Formations
    FORMATIONS_FORMATIONS_INITIALES,
    FORMATIONS_CONTINUES_CATEGORIES,
    FORMATIONS_CONTINUES_SOUS_CATEGORIES,
    FORMATIONS_CONTINUES_FORMATIONS,
    FORMATIONS_CONTINUES_DEVIS,

    // Préinscriptions
    PREINSCRIPTIONS_DEMANDES,
    PREINSCRIPTIONS_PARAMETRES,

    // Communication
    COMMUNICATION_ACTUALITES,
    COMMUNICATION_BANNER_MESSAGES,
    COMMUNICATION_MESSAGES,
    COMMUNICATION_AGENDA,

    // Éditorial
    EDITORIAL_ACTIVITES,
    EDITORIAL_BANNERS,
    EDITORIAL_COMMENTAIRES,
    EDITORIAL_STATISTIQUES,
    EDITORIAL_PARTENAIRES,

    // Administration
    ADMINISTRATION_UTILISATEURS,
    ADMINISTRATION_AUDITS;

    public static boolean isValid(String key) {
        try {
            MenuPermission.valueOf(key);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static Set<String> allKeys() {
        Set<String> keys = new HashSet<>();
        for (MenuPermission p : values()) keys.add(p.name());
        return keys;
    }
}