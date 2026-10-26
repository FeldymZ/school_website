package com.school.api.auth.service;

import com.school.api.auth.dto.MenuModuleDTO;
import com.school.api.auth.dto.MenuModuleRequest;
import com.school.api.auth.entity.MenuModule;
import com.school.api.auth.entity.MenuPermission;
import com.school.api.auth.entity.MenuPermissionRegistry;
import com.school.api.auth.repository.MenuModuleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuModuleService {

    /** Icônes disponibles (mêmes noms côté front, fichier menuDynamique.ts) */
    public static final Set<String> ICONES = Set.of(
            "graduation-cap", "book-open", "clipboard-list", "mail", "newspaper", "settings",
            "folder", "file-text", "users", "calendar", "bar-chart", "briefcase", "globe",
            "image", "message-square", "award", "layers", "star", "layout-grid", "link"
    );

    /** La page « Modules » elle-même : toujours visible pour le SUPERADMIN */
    public static final String CHEMIN_MODULES = "/modules";

    private static final Pattern CLE = Pattern.compile("^[A-Z][A-Z0-9_]{2,59}$");
    private static final Pattern CHEMIN = Pattern.compile("^/[a-z0-9\\-/]*$");

    private final MenuModuleRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    /* =========================
       📄 LECTURE
       ========================= */

    /** Modules visibles dans le menu (pour tous les utilisateurs connectés) */
    @Transactional(readOnly = true)
    public List<MenuModuleDTO> actifs() {
        return repository.findByActifTrueOrderByOrdreAscIdAsc().stream().map(MenuModuleDTO::from).toList();
    }

    /** Tous les modules, y compris masqués (page « Modules ») */
    @Transactional(readOnly = true)
    public List<MenuModuleDTO> tous() {
        return repository.findAllByOrderByOrdreAscIdAsc().stream().map(MenuModuleDTO::from).toList();
    }

    /* =========================
       ➕ CRÉER
       ========================= */
    @Transactional
    public MenuModuleDTO creer(MenuModuleRequest r) {

        MenuModule m = new MenuModule();
        m.setSysteme(false);
        m.setActif(r.actif() == null || r.actif());
        appliquer(m, r, true);

        if (repository.existsByChemin(m.getChemin())) {
            throw new IllegalArgumentException("Un module utilise déjà le lien " + m.getChemin());
        }

        m.setOrdre(repository.maxOrdre() + 10);

        MenuModule saved = repository.save(m);
        rafraichirRegistre();
        return MenuModuleDTO.from(saved);
    }

    /* =========================
       ✏️ MODIFIER
       ========================= */
    @Transactional
    public MenuModuleDTO modifier(Long id, MenuModuleRequest r) {

        MenuModule m = trouver(id);
        boolean systeme = Boolean.TRUE.equals(m.getSysteme());

        if (systeme) {
            /* Module d'origine : le lien, la clé et « réservé SUPERADMIN » ne changent pas */
            String chemin = normaliser(r.chemin());
            if (chemin != null && !chemin.equals(m.getChemin())) {
                throw new IllegalArgumentException("Le lien d'un module d'origine ne peut pas être modifié");
            }
            r = new MenuModuleRequest(
                    m.getCle(), r.libelle(), m.getChemin(), r.section(), r.groupe(), r.icone(),
                    r.actif(), m.getSuperAdminSeulement()
            );
        }

        appliquer(m, r, !systeme);

        if (repository.existsByCheminAndIdNot(m.getChemin(), m.getId())) {
            throw new IllegalArgumentException("Un module utilise déjà le lien " + m.getChemin());
        }
        if (r.actif() != null) {
            m.setActif(r.actif());
        }
        proteger(m);

        MenuModule saved = repository.save(m);
        rafraichirRegistre();
        return MenuModuleDTO.from(saved);
    }

    /* =========================
       👁️ AFFICHER / MASQUER
       ========================= */
    @Transactional
    public MenuModuleDTO changerActif(Long id, boolean actif) {
        MenuModule m = trouver(id);
        if (!actif && CHEMIN_MODULES.equals(m.getChemin())) {
            throw new IllegalStateException("La page « Modules » ne peut pas être masquée");
        }
        m.setActif(actif);
        return MenuModuleDTO.from(repository.save(m));
    }

    /* =========================
       ↕️ RÉORDONNER (une section)
       Les modules reprennent les mêmes positions, dans le nouvel ordre.
       ========================= */
    @Transactional
    public void reordonner(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        List<MenuModule> modules = repository.findAllById(ids);
        if (modules.size() != new HashSet<>(ids).size()) {
            throw new IllegalArgumentException("Module introuvable dans la liste");
        }

        Map<Long, MenuModule> parId = new HashMap<>();
        modules.forEach(m -> parId.put(m.getId(), m));

        List<Integer> positions = modules.stream().map(MenuModule::getOrdre).sorted().toList();

        for (int i = 0; i < ids.size(); i++) {
            parId.get(ids.get(i)).setOrdre(positions.get(i));
        }
        repository.saveAll(modules);
    }

    /* =========================
       🗑️ SUPPRIMER
       ========================= */
    @Transactional
    public void supprimer(Long id) {

        MenuModule m = trouver(id);

        if (Boolean.TRUE.equals(m.getSysteme())) {
            throw new IllegalStateException("Un module d'origine ne peut pas être supprimé (vous pouvez le masquer)");
        }

        String cle = m.getCle();
        repository.delete(m);
        repository.flush();

        /* Clé plus utilisée nulle part → on la retire aussi des accès des utilisateurs */
        if (cle != null && repository.countByCle(cle) == 0 && !MenuPermission.isNative(cle)) {
            int retires = entityManager
                    .createNativeQuery("DELETE FROM user_menu_access WHERE menu_key = :cle")
                    .setParameter("cle", cle)
                    .executeUpdate();
            log.info("🧹 Clé {} retirée de {} utilisateur(s)", cle, retires);
        }

        rafraichirRegistre();
    }

    /* =========================
       🔑 CLÉS ACCEPTÉES PAR LE SERVEUR
       ========================= */
    @Transactional(readOnly = true)
    public void rafraichirRegistre() {
        MenuPermissionRegistry.remplacer(new HashSet<>(repository.toutesLesCles()));
    }

    /* =========================
       HELPERS
       ========================= */

    private MenuModule trouver(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Module introuvable"));
    }

    /** Copie et contrôle les champs de la requête vers le module */
    private void appliquer(MenuModule m, MenuModuleRequest r, boolean lienEtCleModifiables) {

        String libelle = normaliser(r.libelle());
        String section = normaliser(r.section());
        String groupe = normaliser(r.groupe());
        String icone = normaliser(r.icone());
        boolean superAdmin = Boolean.TRUE.equals(r.superAdminSeulement());

        if (libelle == null || libelle.length() > 80) {
            throw new IllegalArgumentException("Le nom du module est obligatoire (80 caractères maximum)");
        }
        if (section == null || section.length() > 60) {
            throw new IllegalArgumentException("La section est obligatoire (60 caractères maximum)");
        }
        if (groupe != null && groupe.length() > 60) {
            throw new IllegalArgumentException("Le sous-menu ne doit pas dépasser 60 caractères");
        }
        if (icone != null && !ICONES.contains(icone)) {
            throw new IllegalArgumentException("Icône inconnue : " + icone);
        }

        if (lienEtCleModifiables) {
            String chemin = normaliser(r.chemin());
            if (chemin == null || chemin.length() > 150 || !CHEMIN.matcher(chemin).matches()) {
                throw new IllegalArgumentException(
                        "Lien invalide : il doit commencer par / et ne contenir que des minuscules, chiffres, - et /");
            }
            String cle = superAdmin ? null : normaliser(r.cle());
            if (cle != null) {
                cle = cle.toUpperCase(Locale.ROOT);
                if (!CLE.matcher(cle).matches()) {
                    throw new IllegalArgumentException(
                            "Clé invalide : MAJUSCULES, chiffres et _ uniquement (ex. COMMUNICATION_EVENEMENTS)");
                }
            }
            m.setChemin(chemin);
            m.setCle(cle);
        }

        m.setLibelle(libelle);
        m.setSection(section);
        m.setGroupe(groupe);
        m.setIcone(icone);
        m.setSuperAdminSeulement(superAdmin);
    }

    /** La page « Modules » reste toujours visible et réservée au SUPERADMIN */
    private void proteger(MenuModule m) {
        if (CHEMIN_MODULES.equals(m.getChemin())) {
            m.setActif(true);
            m.setSuperAdminSeulement(true);
            m.setCle(null);
        }
    }

    private static String normaliser(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}