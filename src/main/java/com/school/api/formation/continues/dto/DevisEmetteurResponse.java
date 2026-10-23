package com.school.api.formation.continues.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DevisEmetteurResponse {
    private Long id;
    private String nom;
    private String fonction;
    private String signatureUrl;
    private String cachetUrl;
    private boolean actif;
    private long nbDevis;
}