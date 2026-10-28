package com.school.api.formation.continues.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Contenu modifiable de la page publique « Sessions de formation » :
 * image d'arrière-plan et textes de l'en-tête.
 * Une seule ligne en base (id = 1).
 */
@Entity
@Table(name = "page_sessions_formation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageSessionsFormation {

    public static final long ID_UNIQUE = 1L;

    @Id
    private Long id;

    /** Petit texte au-dessus du titre (ex. « Formations continues ») */
    @Column(length = 80)
    private String badge;

    @Column(length = 150)
    private String titre;

    @Column(length = 500)
    private String sousTitre;

    /** Chemin de l'image d'arrière-plan (null = dégradé bleu seul) */
    @Column(length = 500)
    private String image;

    private LocalDateTime dateModification;

    @Column(length = 150)
    private String modifiePar;
}