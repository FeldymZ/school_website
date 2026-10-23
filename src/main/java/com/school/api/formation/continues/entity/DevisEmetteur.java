package com.school.api.formation.continues.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** Validateur des devis : nom, fonction, signature et cachet */
@Entity
@Table(name = "devis_emetteur")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevisEmetteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String fonction;

    /* Image de la signature (obligatoire) */
    @Column(nullable = false)
    private String signatureUrl;

    /* Photo du cachet (facultative) */
    private String cachetUrl;

    /* Validateur proposé par défaut */
    @Builder.Default
    private boolean actif = false;

    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();
}