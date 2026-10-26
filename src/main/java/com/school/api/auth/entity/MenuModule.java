package com.school.api.auth.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Un module = un lien du menu du back office (vers une page existante)
 * + la clé d'accès qui permet de le donner (ou non) à un ADMIN.
 *
 * Plusieurs modules peuvent partager la même clé
 * (ex. « Devis » et « Validateurs des devis »).
 */
@Entity
@Table(
        name = "menu_modules",
        uniqueConstraints = @UniqueConstraint(name = "uk_menu_modules_chemin", columnNames = "chemin")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Clé d'accès (ex. FORMATIONS_CONTINUES_DEVIS). null = pas de case d'accès */
    @Column(length = 60)
    private String cle;

    /** Texte du lien dans le menu (ex. « Devis ») */
    @Column(nullable = false, length = 80)
    private String libelle;

    /** Page ouverte par le lien (ex. /demandes-devis) */
    @Column(nullable = false, length = 150)
    private String chemin;

    /** Titre de section dans le menu (ex. « Formations ») */
    @Column(nullable = false, length = 60)
    private String section;

    /** Sous-menu dépliable (ex. « Formations continues »). null = lien direct */
    @Column(length = 60)
    private String groupe;

    /** Nom de l'icône (voir MenuModuleService.ICONES) */
    @Column(length = 40)
    private String icone;

    /** Position dans le menu (plus petit = plus haut) */
    @Column(nullable = false)
    private Integer ordre;

    /** false = masqué du menu */
    @Builder.Default
    @Column(nullable = false)
    private Boolean actif = true;

    /** true = module d'origine : lien et clé verrouillés, suppression impossible */
    @Builder.Default
    @Column(nullable = false)
    private Boolean systeme = false;

    /** true = visible uniquement par les SUPERADMIN (pas de case d'accès) */
    @Builder.Default
    @Column(nullable = false)
    private Boolean superAdminSeulement = false;
}