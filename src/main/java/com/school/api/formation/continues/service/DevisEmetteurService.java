package com.school.api.formation.continues.service;

import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.common.storage.FileStorageService;
import com.school.api.formation.continues.dto.DevisEmetteurResponse;
import com.school.api.formation.continues.entity.DevisEmetteur;
import com.school.api.formation.continues.repository.DevisEmetteurRepository;
import com.school.api.formation.continues.repository.DevisFormationContinuesRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Gestion des validateurs de devis (nom, fonction, signature, cachet) */
@Slf4j
@Service
@RequiredArgsConstructor
public class DevisEmetteurService {

    private final DevisEmetteurRepository emetteurRepo;
    private final DevisFormationContinuesRepository devisRepo;
    private final FileStorageService fileStorageService;

    @PersistenceContext
    private EntityManager entityManager;

    /* ================= GET ALL ================= */
    @Transactional(readOnly = true)
    public List<DevisEmetteurResponse> getAll() {
        return emetteurRepo.findAllOrdered().stream().map(this::toResponse).toList();
    }

    /* ================= PAR DÉFAUT ================= */
    @Transactional(readOnly = true)
    public DevisEmetteurResponse getActive() {
        return emetteurRepo.findByActifTrue().map(this::toResponse).orElse(null);
    }

    /* ================= CREATE ================= */
    @Transactional
    public void create(String nom, String fonction, MultipartFile signature, MultipartFile cachet) {

        verifierTexte(nom, "Nom");
        verifierTexte(fonction, "Fonction");

        if (signature == null || signature.isEmpty()) {
            throw new IllegalArgumentException("Signature obligatoire");
        }
        verifierImage(signature, "La signature");
        if (cachet != null && !cachet.isEmpty()) verifierImage(cachet, "Le cachet");

        emetteurRepo.save(
                DevisEmetteur.builder()
                        .nom(nom.trim())
                        .fonction(fonction.trim())
                        .signatureUrl(fileStorageService.storeSignature(signature))
                        .cachetUrl(cachet != null && !cachet.isEmpty() ? fileStorageService.storeSignature(cachet) : null)
                        .actif(false)
                        .build()
        );
    }

    /* ================= UPDATE ================= */
    @Transactional
    public void update(Long id, String nom, String fonction,
                       MultipartFile signature, MultipartFile cachet, boolean supprimerCachet) {

        DevisEmetteur em = trouver(id);

        verifierTexte(nom, "Nom");
        verifierTexte(fonction, "Fonction");

        em.setNom(nom.trim());
        em.setFonction(fonction.trim());

        /* Les anciens fichiers sont conservés : ils restent utilisés par les devis déjà validés */
        if (signature != null && !signature.isEmpty()) {
            verifierImage(signature, "La signature");
            em.setSignatureUrl(fileStorageService.storeSignature(signature));
        }

        if (cachet != null && !cachet.isEmpty()) {
            verifierImage(cachet, "Le cachet");
            em.setCachetUrl(fileStorageService.storeSignature(cachet));
        } else if (supprimerCachet) {
            em.setCachetUrl(null);
        }
    }

    /* ================= DELETE ================= */
    /**
     * 🆕 Suppression possible même pour le validateur par défaut
     * et même s'il est déjà utilisé dans des devis.
     * Les devis gardent leur COPIE du nom, de la fonction, de la signature et du cachet :
     * on retire seulement leur lien vers ce validateur.
     */
    @Transactional
    public void delete(Long id) {

        DevisEmetteur em = trouver(id);
        long nbDevis = devisRepo.countByEmetteur_Id(id);

        /* 1. Les devis déjà validés gardent leur copie : on retire seulement le lien */
        if (nbDevis > 0) {
            entityManager.createQuery(
                            "update DevisFormationContinues d set d.emetteur = null where d.emetteur.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
        }

        /* 2. Suppression du validateur (s'il était par défaut, il n'y a plus de validateur par défaut) */
        emetteurRepo.delete(em);

        /* 3. Fichiers : supprimés seulement si aucun devis ne s'en sert pour son PDF */
        if (nbDevis == 0) {
            fileStorageService.deleteQuietly(em.getSignatureUrl());
            if (em.getCachetUrl() != null) fileStorageService.deleteQuietly(em.getCachetUrl());
        }

        log.info("🗑️ Validateur « {} » supprimé ({} devis détaché(s){})",
                em.getNom(), nbDevis, em.isActif() ? ", était le validateur par défaut" : "");
    }

    /* ================= 🆕 RETIRER LE STATUT « PAR DÉFAUT » ================= */
    /** Plus de validateur par défaut : il faudra choisir le validateur à chaque devis */
    @Transactional
    public void deactivate(Long id) {

        DevisEmetteur em = trouver(id);

        if (!em.isActif()) {
            throw new IllegalStateException("Ce validateur n'est pas le validateur par défaut");
        }
        em.setActif(false);
    }

    /* ================= DÉFINIR PAR DÉFAUT ================= */
    @Transactional
    public void activate(Long id) {

        /* On désactive d'abord, PUIS on charge le validateur :
           évite qu'un validateur déjà actif se retrouve désactivé */
        emetteurRepo.deactivateAll();

        DevisEmetteur em = trouver(id);
        em.setActif(true);
    }

    /* ================= HELPERS ================= */
    private DevisEmetteur trouver(Long id) {
        return emetteurRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Validateur", "id", id));
    }

    private void verifierTexte(String valeur, String champ) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException(champ + " obligatoire");
        }
    }

    private void verifierImage(MultipartFile f, String label) {
        String type = f.getContentType();
        if (type == null || !(type.equals("image/png") || type.equals("image/jpeg"))) {
            throw new IllegalArgumentException(label + " doit être une image PNG ou JPG");
        }
    }

    private DevisEmetteurResponse toResponse(DevisEmetteur e) {
        return DevisEmetteurResponse.builder()
                .id(e.getId())
                .nom(e.getNom())
                .fonction(e.getFonction())
                .signatureUrl(e.getSignatureUrl())
                .cachetUrl(e.getCachetUrl())
                .actif(e.isActif())
                .nbDevis(devisRepo.countByEmetteur_Id(e.getId()))
                .build();
    }
}