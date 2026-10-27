package com.school.api.formation.continues.dto;

import java.util.List;

/** Une formation (même format que la page formation du site) + ses sessions ouvertes */
public record FormationSessionsPublicDTO(
        FormationDTO formation,
        List<SessionPublicDTO> sessions
) {
}