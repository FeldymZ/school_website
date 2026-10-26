package com.school.api.auth.entity;

import java.util.Set;

/**
 * Clés d'accès créées depuis le back office (table menu_modules).
 * Mis à jour par MenuModuleService ; lu par MenuPermission.isValid().
 */
public final class MenuPermissionRegistry {

    private static volatile Set<String> cles = Set.of();

    private MenuPermissionRegistry() {
    }

    public static boolean contient(String cle) {
        return cle != null && cles.contains(cle);
    }

    public static Set<String> cles() {
        return cles;
    }

    public static void remplacer(Set<String> nouvellesCles) {
        cles = Set.copyOf(nouvellesCles);
    }
}