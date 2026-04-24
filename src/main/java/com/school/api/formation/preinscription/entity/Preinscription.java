package com.school.api.formation.preinscription.entity;

import com.school.api.formation.initiale.entity.FormationInitiale;
import com.school.api.formation.initiale.entity.FormationInitialeLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "preinscriptions")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Preinscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String civilite;

    private String nom;
    private String prenom;

    private LocalDate dateNaissance;
    private String lieuNaissance;

    private String nationalite;

    private String email;
    private String telephone;
    private String whatsapp;

    private String niveauDemande;

    @Enumerated(EnumType.STRING)
    private FormationInitialeLevel level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id")
    private FormationInitiale formation;

    private Boolean valide;

    private LocalDateTime createdAt;
}