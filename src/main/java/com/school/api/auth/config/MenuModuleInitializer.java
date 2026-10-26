package com.school.api.auth.config;

import com.school.api.auth.entity.MenuModule;
import com.school.api.auth.repository.MenuModuleRepository;
import com.school.api.auth.service.MenuModuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Au démarrage : crée les modules d'origine (le menu actuel) s'ils n'existent pas,
 * puis charge les clés acceptées par le serveur.
 * Ne modifie jamais un module déjà présent (vos changements sont conservés).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MenuModuleInitializer implements ApplicationRunner {

    private final MenuModuleRepository repository;
    private final MenuModuleService service;

    private record Origine(String cle, String libelle, String chemin, String section,
                           String groupe, String icone, boolean superAdminSeulement) {
    }

    private static final List<Origine> MODULES_ORIGINE = List.of(
            // Formations
            new Origine("FORMATIONS_FORMATIONS_INITIALES", "Formations initiales", "/formations", "Formations", null, "graduation-cap", false),
            new Origine("FORMATIONS_CONTINUES_CATEGORIES", "Catégories", "/categories", "Formations", "Formations continues", "book-open", false),
            new Origine("FORMATIONS_CONTINUES_SOUS_CATEGORIES", "Sous-catégories", "/sous-categories", "Formations", "Formations continues", "book-open", false),
            new Origine("FORMATIONS_CONTINUES_FORMATIONS", "Formations", "/formations-continues", "Formations", "Formations continues", "book-open", false),
            new Origine("FORMATIONS_CONTINUES_DEVIS", "Devis", "/demandes-devis", "Formations", "Formations continues", "book-open", false),
            new Origine("FORMATIONS_CONTINUES_DEVIS", "Validateurs des devis", "/demandes-devis/validateurs", "Formations", "Formations continues", "book-open", false),
            // Préinscriptions
            new Origine("PREINSCRIPTIONS_DEMANDES", "Demandes", "/preinscriptions", "Préinscriptions", "Préinscriptions", "clipboard-list", false),
            new Origine("PREINSCRIPTIONS_PARAMETRES", "Paramètres", "/preinscriptions/configuration", "Préinscriptions", "Préinscriptions", "clipboard-list", false),
            // Communication
            new Origine("COMMUNICATION_ACTUALITES", "Actualités", "/actualites", "Communication", "Communication", "mail", false),
            new Origine("COMMUNICATION_BANNER_MESSAGES", "Pop-up", "/banner-messages", "Communication", "Communication", "mail", false),
            new Origine("COMMUNICATION_MESSAGES", "Messages", "/messages", "Communication", "Communication", "mail", false),
            new Origine("COMMUNICATION_AGENDA", "Agenda", "/agenda", "Communication", "Communication", "mail", false),
            // Éditorial
            new Origine("EDITORIAL_ACTIVITES", "Activités", "/activites", "Éditorial", "Éditorial", "newspaper", false),
            new Origine("EDITORIAL_BANNERS", "Bannières", "/banners", "Éditorial", "Éditorial", "newspaper", false),
            new Origine("EDITORIAL_COMMENTAIRES", "Commentaires", "/commentaires", "Éditorial", "Éditorial", "newspaper", false),
            new Origine("EDITORIAL_STATISTIQUES", "Statistiques", "/statistiques", "Éditorial", "Éditorial", "newspaper", false),
            new Origine("EDITORIAL_PARTENAIRES", "Partenaires", "/partenaires", "Éditorial", "Éditorial", "newspaper", false),
            // Administration
            new Origine("ADMINISTRATION_UTILISATEURS", "Utilisateurs", "/utilisateurs", "Administration", "Système", "settings", false),
            new Origine("ADMINISTRATION_AUDITS", "Audits", "/configuration", "Administration", "Système", "settings", false),
            new Origine(null, "Modules", MenuModuleService.CHEMIN_MODULES, "Administration", "Système", "settings", true)
    );

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        int ajoutes = 0;
        int ordre = 10;

        for (Origine o : MODULES_ORIGINE) {
            if (!repository.existsByChemin(o.chemin())) {
                repository.save(MenuModule.builder()
                        .cle(o.cle())
                        .libelle(o.libelle())
                        .chemin(o.chemin())
                        .section(o.section())
                        .groupe(o.groupe())
                        .icone(o.icone())
                        .ordre(ordre)
                        .actif(true)
                        .systeme(true)
                        .superAdminSeulement(o.superAdminSeulement())
                        .build());
                ajoutes++;
            }
            ordre += 10;
        }

        if (ajoutes > 0) {
            log.info("🧩 {} module(s) d'origine ajouté(s) au menu", ajoutes);
        }

        service.rafraichirRegistre();
    }
}