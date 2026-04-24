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

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(nullable = false, length = 150)
    private String fonction; // ex: "Le Directeur Général"

    @Column(nullable = false, length = 500)
    private String signatureUrl; // chemin vers image signature

    @Builder.Default
    @Column(nullable = false)
    private boolean actif = false;
}