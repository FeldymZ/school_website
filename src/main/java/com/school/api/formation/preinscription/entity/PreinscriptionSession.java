package com.school.api.formation.preinscription.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "preinscription_sessions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreinscriptionSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String anneeUniversitaire;

    /* 🔥 NOUVEAU */
    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @Column(nullable = false)
    private LocalDateTime dateFin;

    /* ❌ SUPPRIMÉ */
    // private boolean ouverte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emetteur_id", nullable = false)
    private PreinscriptionEmetteur emetteur;
}