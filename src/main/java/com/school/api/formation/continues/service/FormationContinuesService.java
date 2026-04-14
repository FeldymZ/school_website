package com.school.api.formation.continues.service;

import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.common.storage.FileStorageService;
import com.school.api.formation.continues.dto.CreateFormationContinuesDTO;
import com.school.api.formation.continues.dto.FormationDTO;
import com.school.api.formation.continues.entity.*;
import com.school.api.formation.continues.mapper.FormationMapper;
import com.school.api.formation.continues.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class FormationContinuesService {

    private final FormationContinuesRepository repository;
    private final SousCategorieFormationContinuesRepository sousCategorieRepository;
    private final DemandeDevisLigneFormationContinuesRepository ligneRepository;
    private final FileStorageService fileStorageService;
    private final FormationMapper mapper;

    private final Random random = new Random();

    /* ================= CREATE ================= */

    public FormationDTO create(Long sousCategorieId, CreateFormationContinuesDTO dto) {

        SousCategorieFormationContinues sc = sousCategorieRepository.findById(sousCategorieId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("SousCategorie", "id", sousCategorieId)
                );

        FormationContinues f = new FormationContinues();

        f.setReference(generateReference());
        f.setSlug(generateSlugUnique(dto.getLibelle()));

        f.setLibelle(dto.getLibelle());
        f.setDescription(dto.getDescription());
        f.setObjectifs(dto.getObjectifs());
        f.setCompetences(dto.getCompetences());
        f.setPrix(dto.getPrix());
        f.setDuree(dto.getDuree());

        if (dto.getUniteDuree() != null) {
            try {
                f.setUniteDuree(UniteDuree.valueOf(dto.getUniteDuree().toUpperCase()));
            } catch (Exception e) {
                throw new RuntimeException("Unité de durée invalide");
            }
        }

        f.setLieu(dto.getLieu());
        f.setTitreDelivre(dto.getTitreDelivre());
        f.setSousCategorie(sc);
        f.setEnabled(true);

        if (dto.getCover() != null && !dto.getCover().isEmpty()) {
            validateImage(dto.getCover());
            f.setLogo(fileStorageService.storeFormationContinuesCover(dto.getCover()));
        }

        return mapper.toDTO(repository.save(f));
    }

    /* ================= UPDATE ================= */

    public FormationDTO update(Long id, CreateFormationContinuesDTO dto) {

        FormationContinues f = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Formation", "id", id));

        f.setLibelle(dto.getLibelle());
        f.setDescription(dto.getDescription());
        f.setObjectifs(dto.getObjectifs());
        f.setCompetences(dto.getCompetences());
        f.setPrix(dto.getPrix());
        f.setDuree(dto.getDuree());

        if (dto.getUniteDuree() != null) {
            try {
                f.setUniteDuree(UniteDuree.valueOf(dto.getUniteDuree().toUpperCase()));
            } catch (Exception e) {
                throw new RuntimeException("Unité de durée invalide");
            }
        }

        f.setLieu(dto.getLieu());
        f.setTitreDelivre(dto.getTitreDelivre());

        if (dto.getSousCategorieId() != null) {
            SousCategorieFormationContinues sc =
                    sousCategorieRepository.findById(dto.getSousCategorieId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "SousCategorie",
                                            "id",
                                            dto.getSousCategorieId()
                                    )
                            );

            f.setSousCategorie(sc);
        }

        if (dto.getCover() != null && !dto.getCover().isEmpty()) {
            validateImage(dto.getCover());
            f.setLogo(fileStorageService.storeFormationContinuesCover(dto.getCover()));
        }

        return mapper.toDTO(repository.save(f));
    }

    /* ================= GET ================= */

    public Page<FormationDTO> getAll(int page, int size) {
        return repository.findAll(
                PageRequest.of(page, size, Sort.by("id").descending())
        ).map(mapper::toDTO);
    }

    public FormationDTO getById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Formation", "id", id));
    }

    /* ================= DELETE ================= */

    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Formation", "id", id);
        }

        if (ligneRepository.existsByFormationId(id)) {
            throw new RuntimeException(
                    "Impossible de supprimer cette formation car elle est utilisée dans des devis"
            );
        }

        repository.deleteById(id);
    }

    /* ================= TOGGLE ================= */

    public FormationDTO toggleStatus(Long id) {

        FormationContinues f = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Formation", "id", id));

        f.setEnabled(!f.isEnabled());

        return mapper.toDTO(repository.save(f));
    }

    /* ================= PUBLIC ================= */

    public Page<FormationDTO> getAllPublic(
            int page,
            int size,
            Long categorieId,
            Long sousCategorieId
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<FormationContinues> result;

        if (categorieId != null && sousCategorieId != null) {
            result = repository.findByEnabledTrueAndSousCategorieIdAndSousCategorieCategorieId(
                    sousCategorieId,
                    categorieId,
                    pageable
            );
        } else if (categorieId != null) {
            result = repository.findByEnabledTrueAndSousCategorieCategorieId(
                    categorieId,
                    pageable
            );
        } else if (sousCategorieId != null) {
            result = repository.findByEnabledTrueAndSousCategorieId(
                    sousCategorieId,
                    pageable
            );
        } else {
            result = repository.findByEnabledTrue(pageable);
        }

        return result.map(mapper::toDTO);
    }

    public FormationDTO getBySlug(String slug) {

        FormationContinues f = repository.findBySlug(slug)
                .filter(FormationContinues::isEnabled)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Formation", "slug", slug)
                );

        return mapper.toDTO(f);
    }

    /* ================= UTILS ================= */

    private void validateImage(org.springframework.web.multipart.MultipartFile file) {

        if (!file.getContentType().startsWith("image/")) {
            throw new RuntimeException("Le fichier doit être une image");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("Image trop volumineuse (max 5MB)");
        }
    }

    private String generateSlug(String libelle) {
        return libelle
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    private String generateSlugUnique(String libelle) {

        String base = generateSlug(libelle);
        String slug = base;
        int i = 1;

        while (repository.findBySlug(slug).isPresent()) {
            slug = base + "-" + i++;
        }

        return slug;
    }

    private Integer generateReference() {

        Integer ref;

        do {
            ref = 5000 + random.nextInt(2000);
        } while (repository.findByReference(ref) != null);

        return ref;
    }
}