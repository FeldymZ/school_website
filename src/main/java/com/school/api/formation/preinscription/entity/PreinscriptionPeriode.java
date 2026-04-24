package com.school.api.formation.preinscription.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "preinscription_periode")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PreinscriptionPeriode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    @Column(nullable = false)
    private LocalDateTime dateFin;

    /* 🔥 FIX ICI */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id", nullable = false)
    private SessionUniversitaire session;

    /* 🔥 FIX ICI */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emetteur_id", nullable = false)
    private PreinscriptionEmetteur emetteur;

    @Column(nullable = false)
    private boolean active = true;
}