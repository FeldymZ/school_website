package com.school.api.formation.preinscription.dto;

import lombok.Builder;

@Builder
public record PreinscriptionEmetteurResponse(
        Long id,
        String nom,
        String fonction,
        String signatureUrl,
        boolean actif,
        long nbPeriodes
) {}