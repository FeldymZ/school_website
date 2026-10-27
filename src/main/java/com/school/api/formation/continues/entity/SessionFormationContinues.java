package com.school.api.formation.continues.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Une session (période) d'une formation continue :
 * dates, lieu, horaires et nombre de places.
 * Une formation peut avoir plusieurs sessions.
 */
@Entity
@Table(name = "session_formation_continues")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionFormationContinues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "formation_id", nullable = false)
    private FormationContinues formation;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @Column(length = 150)
    private String lieu;

    /** Ex. « 9h – 12h, du lundi au vendredi » */
    @Column(length = 150)
    private String horaires;

    /** null = places illimitées */
    private Integer nombrePlaces;

    /** false = session masquée sur le site */
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private LocalDateTime dateCreation;
}