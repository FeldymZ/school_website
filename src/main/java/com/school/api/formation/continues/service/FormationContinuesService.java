package com.school.api.formation.continues.service;

import com.school.api.common.storage.FileStorageService;
import com.school.api.formation.continues.dto.CreateFormationContinuesDTO;
import com.school.api.formation.continues.entity.*;
import com.school.api.formation.continues.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FormationContinuesService {

    private final FormationContinuesRepository repository;
    private final SousCategorieFormationContinuesRepository sousCategorieRepository;
    private final FileStorageService fileStorageService;

    /* ================= CREATE ================= */

    public FormationContinues create(Long sousCategorieId, CreateFormationContinuesDTO dto) {

        SousCategorieFormationContinues sc = sousCategorieRepository.findById(sousCategorieId)
                .orElseThrow(() -> new RuntimeException("Sous-catégorie introuvable"));

        FormationContinues f = new FormationContinues();

        /* ===== INFOS ===== */
        f.setLibelle(dto.getLibelle());
        f.setDescription(dto.getDescription());
        f.setObjectifs(dto.getObjectifs());
        f.setCompetences(dto.getCompetences());
        f.setPrix(dto.getPrix());
        f.setDuree(dto.getDuree());

        /* ===== UNITE DUREE (SECURISEE) ===== */
        if (dto.getUniteDuree() != null) {
            try {
                f.setUniteDuree(
                        UniteDuree.valueOf(dto.getUniteDuree().toUpperCase())
                );
            } catch (Exception e) {
                throw new RuntimeException("Unité de durée invalide (JOURS, MOIS, ANNEES)");
            }
        }

        f.setLieu(dto.getLieu());
        f.setTitreDelivre(dto.getTitreDelivre());

        /* ===== RELATION ===== */
        f.setSousCategorie(sc);

        /* ===== ACTIVE ===== */
        f.setEnabled(true);

        /* ===== COVER ===== */
        if (dto.getCover() != null && !dto.getCover().isEmpty()) {
            f.setLogo(fileStorageService.storeFormationContinuesCover(dto.getCover()));
        }

        return repository.save(f);
    }

    /* ================= UPDATE ================= */

    public FormationContinues update(Long id, CreateFormationContinuesDTO dto) {

        FormationContinues f = getById(id);

        /* ===== INFOS ===== */
        f.setLibelle(dto.getLibelle());
        f.setDescription(dto.getDescription());
        f.setObjectifs(dto.getObjectifs());
        f.setCompetences(dto.getCompetences());
        f.setPrix(dto.getPrix());
        f.setDuree(dto.getDuree());

        /* ===== UNITE DUREE (SECURISEE) ===== */
        if (dto.getUniteDuree() != null) {
            try {
                f.setUniteDuree(
                        UniteDuree.valueOf(dto.getUniteDuree().toUpperCase())
                );
            } catch (Exception e) {
                throw new RuntimeException("Unité de durée invalide (JOURS, MOIS, ANNEES)");
            }
        }

        f.setLieu(dto.getLieu());
        f.setTitreDelivre(dto.getTitreDelivre());

        /* ===== COVER ===== */
        if (dto.getCover() != null && !dto.getCover().isEmpty()) {
            fileStorageService.deleteQuietly(f.getLogo());
            f.setLogo(fileStorageService.storeFormationContinuesCover(dto.getCover()));
        }

        return repository.save(f);
    }

    /* ================= GET ================= */

    public Page<FormationContinues> getAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    public Page<FormationContinues> getAllPublic(int page, int size) {
        return repository.findByEnabledTrue(PageRequest.of(page, size));
    }

    public FormationContinues getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation introuvable"));
    }

    /* ================= SEARCH ================= */

    public FormationContinues getByReference(Integer reference) {

        FormationContinues f = repository.findByReference(reference);

        if (f == null) {
            throw new RuntimeException("Formation introuvable");
        }

        return f;
    }

    /* ================= FILTER ================= */

    public Page<FormationContinues> filter(
            Long categorieId,
            Long sousCategorieId,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        if (sousCategorieId != null) {
            return repository.findBySousCategorieId(sousCategorieId, pageable);
        }

        if (categorieId != null) {
            return repository.findBySousCategorieCategorieId(categorieId, pageable);
        }

        return repository.findAll(pageable);
    }

    /* ================= DELETE ================= */

    public void delete(Long id) {

        FormationContinues f = getById(id);

        /* supprimer image */
        fileStorageService.deleteQuietly(f.getLogo());

        repository.deleteById(id);
    }
}