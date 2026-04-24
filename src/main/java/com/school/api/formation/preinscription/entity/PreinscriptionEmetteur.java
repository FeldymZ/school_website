package com.school.api.formation.preinscription.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "preinscription_emetteurs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreinscriptionEmetteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String fonction; // ex: "Le Directeur Général"

    @Column(nullable = false)
    private String signatureUrl; // chemin vers image signature

    @Builder.Default
    @Column(nullable = false)
    private boolean actif = false;
}