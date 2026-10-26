package com.school.api.auth.dto;

import com.school.api.auth.entity.MenuModule;

public record MenuModuleDTO(
        Long id,
        String cle,
        String libelle,
        String chemin,
        String section,
        String groupe,
        String icone,
        Integer ordre,
        boolean actif,
        boolean systeme,
        boolean superAdminSeulement
) {
    public static MenuModuleDTO from(MenuModule m) {
        return new MenuModuleDTO(
                m.getId(),
                m.getCle(),
                m.getLibelle(),
                m.getChemin(),
                m.getSection(),
                m.getGroupe(),
                m.getIcone(),
                m.getOrdre(),
                Boolean.TRUE.equals(m.getActif()),
                Boolean.TRUE.equals(m.getSysteme()),
                Boolean.TRUE.equals(m.getSuperAdminSeulement())
        );
    }
}