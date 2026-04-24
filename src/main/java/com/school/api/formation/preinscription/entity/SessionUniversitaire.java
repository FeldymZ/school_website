package com.school.api.formation.preinscription.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "session_universitaire")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class SessionUniversitaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String annee; // ex: 2025-2026
}