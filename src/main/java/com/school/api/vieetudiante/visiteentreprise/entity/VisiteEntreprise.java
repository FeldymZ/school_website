package com.school.api.vieetudiante.visiteentreprise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "visites_entreprise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisiteEntreprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @ElementCollection
    @CollectionTable(
            name = "visite_entreprise_photos",
            joinColumns = @JoinColumn(name = "visite_id")
    )
    @Column(name = "photo_url")
    private List<String> photos;

    @Column(nullable = false)
    private LocalDateTime datePublication;

    @Column(nullable = false)
    private boolean published;
}
