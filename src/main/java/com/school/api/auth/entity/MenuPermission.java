package com.school.api.auth.entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Clés d'accès d'origine.
 * Les modules ajoutés depuis le back office (table menu_modules)
 * sont aussi acceptés grâce à MenuPermissionRegistry.
 */
public enum MenuPermission {

    // Formations
    FORMATIONS_FORMATIONS_INITIALES,
    FORMATIONS_CONTINUES_CATEGORIES,
    FORMATIONS_CONTINUES_SOUS_CATEGORIES,
    FORMATIONS_CONTINUES_FORMATIONS,
    FORMATIONS_CONTINUES_DEVIS,
    FORMATIONS_CONTINUES_VALIDATEURS,   // 🆕 « Validateurs des devis » (séparé de « Devis »)

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

    /** true si la clé fait partie de l'enum (clé d'origine) */
    public static boolean isNative(String key) {
        if (key == null) return false;
        try {
            MenuPermission.valueOf(key);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /** Clé d'origine OU clé d'un module créé dans le back office */
    public static boolean isValid(String key) {
        return isNative(key) || MenuPermissionRegistry.contient(key);
    }

    public static Set<String> allKeys() {
        Set<String> keys = new HashSet<>();
        for (MenuPermission p : values()) keys.add(p.name());
        keys.addAll(MenuPermissionRegistry.cles());
        return keys;
    }
}