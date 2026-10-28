package com.school.api.formation.continues.service;

import com.school.api.common.storage.FileStorageService;
import com.school.api.formation.continues.dto.PageSessionsFormationDTO;
import com.school.api.formation.continues.entity.PageSessionsFormation;
import com.school.api.formation.continues.repository.PageSessionsFormationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Textes et image d'arrière-plan de la page publique « Sessions de formation ».
 */
@Service
@RequiredArgsConstructor
public class PageSessionsFormationService {

    /* Textes affichés tant que rien n'a été modifié */
    public static final String BADGE_DEFAUT = "Formations continues";
    public static final String TITRE_DEFAUT = "Sessions de formation";
    public static final String SOUS_TITRE_DEFAUT =
            "Découvrez les prochaines sessions programmées et inscrivez-vous directement en ligne.";

    private static final long TAILLE_MAX = 5L * 1024 * 1024; // 5 Mo

    private final PageSessionsFormationRepository repository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public PageSessionsFormationDTO lire() {
        return repository.findById(PageSessionsFormation.ID_UNIQUE)
                .map(this::toDTO)
                .orElse(new PageSessionsFormationDTO(BADGE_DEFAUT, TITRE_DEFAUT, SOUS_TITRE_DEFAUT, null, null, null));
    }

    @Transactional
    public PageSessionsFormationDTO modifier(String badge, String titre, String sousTitre,
                                             MultipartFile image, boolean supprimerImage, String admin) {

        PageSessionsFormation p = repository.findById(PageSessionsFormation.ID_UNIQUE)
                .orElseGet(() -> PageSessionsFormation.builder().id(PageSessionsFormation.ID_UNIQUE).build());

        String t = texte(titre, 150);
        if (t == null) throw new RuntimeException("Le titre est obligatoire");

        p.setBadge(texte(badge, 80));
        p.setTitre(t);
        p.setSousTitre(texte(sousTitre, 500));

        /* Nouvelle image : on enregistre la nouvelle puis on supprime l'ancienne */
        if (image != null && !image.isEmpty()) {
            if (image.getContentType() == null || !image.getContentType().startsWith("image/")) {
                throw new RuntimeException("Le fichier doit être une image (JPG, PNG ou WEBP)");
            }
            if (image.getSize() > TAILLE_MAX) {
                throw new RuntimeException("Image trop volumineuse (5 Mo maximum)");
            }
            String ancienne = p.getImage();
            p.setImage(fileStorageService.storeFormationContinuesCover(image));
            if (ancienne != null) fileStorageService.deleteQuietly(ancienne);
        } else if (supprimerImage && p.getImage() != null) {
            fileStorageService.deleteQuietly(p.getImage());
            p.setImage(null);
        }

        p.setDateModification(LocalDateTime.now(ZoneId.of("Africa/Libreville")));
        p.setModifiePar(admin);

        return toDTO(repository.save(p));
    }

    private static String texte(String v, int max) {
        if (v == null || v.isBlank()) return null;
        String t = v.trim();
        if (t.length() > max) throw new RuntimeException("Texte trop long (" + max + " caractères maximum)");
        return t;
    }

    private PageSessionsFormationDTO toDTO(PageSessionsFormation p) {
        return new PageSessionsFormationDTO(
                p.getBadge(),
                p.getTitre() != null ? p.getTitre() : TITRE_DEFAUT,
                p.getSousTitre(),
                p.getImage(),
                p.getDateModification(),
                p.getModifiePar());
    }
}