package com.school.api.auth.config;

import com.school.api.auth.entity.MenuModule;
import com.school.api.auth.repository.MenuModuleRepository;
import com.school.api.auth.service.MenuModuleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

    /* 🆕 « Devis » et « Validateurs des devis » ont chacun leur accès */
    private static final String CLE_DEVIS = "FORMATIONS_CONTINUES_DEVIS";
    private static final String CLE_VALIDATEURS = "FORMATIONS_CONTINUES_VALIDATEURS";
    private static final String CHEMIN_VALIDATEURS = "/demandes-devis/validateurs";

    private record Origine(String cle, String libelle, String chemin, String section,
                           String groupe, String icone, boolean superAdminSeulement) {
    }

    private static final List<Origine> MODULES_ORIGINE = List.of(
            // Formations
            new Origine("FORMATIONS_FORMATIONS_INITIALES", "Formations initiales", "/formations", "Formations", null, "graduation-cap", false),
            // 🆕 Formations continues : chaque module est un lien séparé (pas de sous-menu)
            new Origine("FORMATIONS_CONTINUES_CATEGORIES", "Catégories", "/categories", "Formations continues", null, "folder", false),
            new Origine("FORMATIONS_CONTINUES_SOUS_CATEGORIES", "Sous-catégories", "/sous-categories", "Formations continues", null, "layers", false),
            new Origine("FORMATIONS_CONTINUES_FORMATIONS", "Formations", "/formations-continues", "Formations continues", null, "book-open", false),
            new Origine("FORMATIONS_CONTINUES_DEVIS", "Devis", "/demandes-devis", "Formations continues", null, "file-text", false),
            new Origine(CLE_VALIDATEURS, "Validateurs des devis", "/demandes-devis/validateurs", "Formations continues", null, "award", false),
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

        /* 🆕 Séparation Devis / Validateurs (une seule fois) :
           - le module « Validateurs des devis » déjà créé avec la clé Devis reçoit sa propre clé ;
           - les ADMIN qui avaient « Devis » gardent l'accès aux validateurs. */
        boolean donnerAccesValidateurs = !repository.existsByChemin(CHEMIN_VALIDATEURS);
        for (MenuModule m : repository.findAll()) {
            if (CHEMIN_VALIDATEURS.equals(m.getChemin()) && CLE_DEVIS.equals(m.getCle())) {
                m.setCle(CLE_VALIDATEURS);
                repository.save(m);
                donnerAccesValidateurs = true;
            }
        }

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

        separerFormationsContinues();

        if (donnerAccesValidateurs) {
            int copies = entityManager.createNativeQuery("""
                    INSERT INTO user_menu_access (user_id, menu_key)
                    SELECT a.user_id, :validateurs
                    FROM user_menu_access a
                    WHERE a.menu_key = :devis
                      AND NOT EXISTS (
                          SELECT 1 FROM user_menu_access b
                          WHERE b.user_id = a.user_id AND b.menu_key = :validateurs
                      )
                    """)
                    .setParameter("validateurs", CLE_VALIDATEURS)
                    .setParameter("devis", CLE_DEVIS)
                    .executeUpdate();
            log.info("🔑 Accès « Validateurs des devis » donné à {} ADMIN qui avaient « Devis »", copies);
        }

        service.rafraichirRegistre();
    }

    /**
     * 🆕 Mise à jour (une seule fois) : les 5 modules des formations continues
     * étaient regroupés dans un sous-menu « Formations continues ».
     * Ils deviennent des liens séparés dans une section « Formations continues ».
     * Ne s'applique que si les 5 sont encore dans l'ancien sous-menu
     * (vos changements faits ensuite dans la page « Modules » sont conservés).
     */
    private void separerFormationsContinues() {
        java.util.Map<String, String> icones = java.util.Map.of(
                "/categories", "folder",
                "/sous-categories", "layers",
                "/formations-continues", "book-open",
                "/demandes-devis", "file-text",
                CHEMIN_VALIDATEURS, "award"
        );

        List<MenuModule> anciens = repository.findAll().stream()
                .filter(m -> icones.containsKey(m.getChemin()))
                .filter(m -> Boolean.TRUE.equals(m.getSysteme()))
                .filter(m -> "Formations continues".equals(m.getGroupe()) && "Formations".equals(m.getSection()))
                .toList();

        if (anciens.size() != icones.size()) return; // déjà fait, ou modifié à la main

        anciens.forEach(m -> {
            m.setSection("Formations continues");
            m.setGroupe(null);
            m.setIcone(icones.get(m.getChemin()));
        });
        repository.saveAll(anciens);
        log.info("📂 Formations continues : {} modules séparés dans le menu", anciens.size());
    }
}