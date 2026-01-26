package com.school.api.vieetudiante.visiteentreprise.service;

import com.school.api.common.storage.FileStorageService;
import com.school.api.vieetudiante.visiteentreprise.dto.VisiteEntrepriseResponse;
import com.school.api.vieetudiante.visiteentreprise.entity.VisiteEntreprise;
import com.school.api.vieetudiante.visiteentreprise.repository.VisiteEntrepriseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisiteEntrepriseService {

    private final VisiteEntrepriseRepository repository;
    private final FileStorageService fileStorageService;

    /* =========================
       PUBLIC
       ========================= */

    public List<VisiteEntrepriseResponse> getAllPublished() {
        return repository.findByPublishedTrueOrderByDatePublicationDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public VisiteEntrepriseResponse getPublishedById(Long id) {
        VisiteEntreprise visite = repository.findById(id)
                .filter(VisiteEntreprise::isPublished)
                .orElseThrow(() -> new RuntimeException("Contenu introuvable"));

        return toResponse(visite);
    }

    /* =========================
       ADMIN
       ========================= */

    /** CREATE */
    public VisiteEntreprise create(
            String titre,
            String contenu,
            MultipartFile[] photos,
            LocalDateTime datePublication,
            boolean published
    ) {
        List<String> photoUrls = storePhotos(photos);

        VisiteEntreprise visite = VisiteEntreprise.builder()
                .titre(titre)
                .contenu(contenu)
                .photos(photoUrls)
                .datePublication(datePublication)
                .published(published)
                .build();

        return repository.save(visite);
    }

    /** READ ALL (ADMIN) */
    public List<VisiteEntreprise> getAllAdmin() {
        return repository.findAll();
    }

    /** READ ONE (ADMIN) */
    public VisiteEntreprise getByIdAdmin(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Visite d’entreprise introuvable"));
    }

    /** UPDATE */
    public VisiteEntreprise update(
            Long id,
            String titre,
            String contenu,
            MultipartFile[] photos,
            LocalDateTime datePublication,
            boolean published
    ) {
        VisiteEntreprise visite = getByIdAdmin(id);

        visite.setTitre(titre);
        visite.setContenu(contenu);
        visite.setDatePublication(datePublication);
        visite.setPublished(published);

        if (photos != null && photos.length > 0) {
            List<String> photoUrls = storePhotos(photos);
            visite.setPhotos(photoUrls);
        }

        return repository.save(visite);
    }

    /** DELETE */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Visite d’entreprise introuvable");
        }
        repository.deleteById(id);
    }

    /* =========================
       UTIL / MAPPING
       ========================= */

    private List<String> storePhotos(MultipartFile[] photos) {
        List<String> photoUrls = new ArrayList<>();

        if (photos != null) {
            for (MultipartFile photo : photos) {
                photoUrls.add(
                        fileStorageService.storeVisiteEntrepriseImage(photo)
                );
            }
        }
        return photoUrls;
    }

    private VisiteEntrepriseResponse toResponse(VisiteEntreprise v) {
        return new VisiteEntrepriseResponse(
                v.getId(),
                v.getTitre(),
                v.getContenu(),
                v.getPhotos(),
                v.getDatePublication()
        );
    }
}
