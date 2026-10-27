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
                           String groupe, String icone, boolean superAdminSeulement, Integer ordreFixe) {
        Origine(String cle, String libelle, String chemin, String section,
                String groupe, String icone, boolean superAdminSeulement) {
            this(cle, libelle, chemin, section, groupe, icone, superAdminSeulement, null);
        }
    }

    private static final List<Origine> MODULES_ORIGINE = List.of(
            // Formations
            new Origine("FORMATIONS_FORMATIONS_INITIALES", "Formations initiales", "/formations", "Formations", null, "graduation-cap", false),
            new Origine("FORMATIONS_CONTINUES_CATEGORIES", "Catégories", "/categories", "Formations", "Formations continues", "book-open", false),
            new Origine("FORMATIONS_CONTINUES_SOUS_CATEGORIES", "Sous-catégories", "/sous-categories", "Formations", "Formations continues", "book-open", false),
            new Origine("FORMATIONS_CONTINUES_FORMATIONS", "Formations", "/formations-continues", "Formations", "Formations continues", "book-open", false),
            new Origine("FORMATIONS_CONTINUES_DEVIS", "Devis", "/demandes-devis", "Formations", "Formations continues", "book-open", false),
            // 🆕 Validateurs des devis : accès séparé de « Devis »
            new Origine(CLE_VALIDATEURS, "Validateurs des devis", "/demandes-devis/validateurs", "Formations", "Formations continues", "book-open", false),
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
            new Origine(null, "Modules", MenuModuleService.CHEMIN_MODULES, "Administration", "Système", "settings", true),
            // 🆕 Session de formation (placé juste après les Formations continues)
            new Origine("FORMATIONS_SESSIONS_AGENDA", "Agenda de formation", "/sessions-formation/agenda", "Formations", "Session de formation", "calendar", false, 62),
            new Origine("FORMATIONS_SESSIONS_PARTICIPATIONS", "Demandes de participation", "/sessions-formation/participations", "Formations", "Session de formation", "calendar", false, 64)
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
                        .ordre(o.ordreFixe() != null ? o.ordreFixe() : ordre)
                        .actif(true)
                        .systeme(true)
                        .superAdminSeulement(o.superAdminSeulement())
                        .build());
                ajoutes++;
            }
            if (o.ordreFixe() == null) ordre += 10;
        }

        if (ajoutes > 0) {
            log.info("🧩 {} module(s) d'origine ajouté(s) au menu", ajoutes);
        }

        regrouperFormationsContinues();

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
     * Remet les 5 modules des formations continues dans le sous-menu dépliable
     * « Formations continues » (design d'origine de la barre de gauche),
     * s'ils avaient été sortis en liens séparés par la version précédente.
     * Chaque module garde sa propre case d'accès.
     */
    private void regrouperFormationsContinues() {
        java.util.Map<String, String> separes = java.util.Map.of(
                "/categories", "folder",
                "/sous-categories", "layers",
                "/formations-continues", "book-open",
                "/demandes-devis", "file-text",
                CHEMIN_VALIDATEURS, "award"
        );

        List<MenuModule> modules = repository.findAll().stream()
                .filter(m -> separes.containsKey(m.getChemin()))
                .filter(m -> Boolean.TRUE.equals(m.getSysteme()))
                .filter(m -> m.getGroupe() == null && "Formations continues".equals(m.getSection()))
                .filter(m -> separes.get(m.getChemin()).equals(m.getIcone()))
                .toList();

        if (modules.size() != separes.size()) return; // rien à faire

        modules.forEach(m -> {
            m.setSection("Formations");
            m.setGroupe("Formations continues");
            m.setIcone("book-open");
        });
        repository.saveAll(modules);
        log.info("📂 Formations continues : {} modules remis dans le sous-menu", modules.size());
    }
}