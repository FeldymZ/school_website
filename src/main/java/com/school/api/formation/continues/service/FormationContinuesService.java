package com.school.api.formation.continues.service;

import com.school.api.common.storage.FileStorageService;
import com.school.api.common.exception.ResourceNotFoundException;
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

        f.setLibelle(dto.getLibelle());
        f.setDescription(dto.getDescription());
        f.setObjectifs(dto.getObjectifs());
        f.setCompetences(dto.getCompetences());
        f.setPrix(dto.getPrix());
        f.setDuree(dto.getDuree());

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
        f.setSousCategorie(sc);
        f.setEnabled(true);

        if (dto.getCover() != null && !dto.getCover().isEmpty()) {
            f.setLogo(fileStorageService.storeFormationContinuesCover(dto.getCover()));
        }

        return mapper.toDTO(repository.save(f));
    }

    /* ================= UPDATE ================= */

    public FormationDTO update(Long id, CreateFormationContinuesDTO dto) {

        FormationContinues f = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Formation", "id", id)
                );

        f.setLibelle(dto.getLibelle());
        f.setDescription(dto.getDescription());
        f.setObjectifs(dto.getObjectifs());
        f.setCompetences(dto.getCompetences());
        f.setPrix(dto.getPrix());
        f.setDuree(dto.getDuree());

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

        if (dto.getCover() != null && !dto.getCover().isEmpty()) {
            fileStorageService.deleteQuietly(f.getLogo());
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

    public Page<FormationDTO> getAllPublic(int page, int size) {
        return repository.findByEnabledTrue(
                PageRequest.of(page, size, Sort.by("id").descending())
        ).map(mapper::toDTO);
    }

    public FormationDTO getById(Long id) {
        return mapper.toDTO(
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Formation", "id", id)
                        )
        );
    }

    /* ================= SEARCH ================= */

    public FormationDTO getByReference(Integer reference) {

        FormationContinues f = repository.findByReference(reference);

        if (f == null) {
            throw new ResourceNotFoundException("Formation", "reference", reference);
        }

        return mapper.toDTO(f);
    }

    /* ================= FILTER ================= */

    public Page<FormationDTO> filter(
            Long categorieId,
            Long sousCategorieId,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        if (sousCategorieId != null) {
            return repository.findBySousCategorieId(sousCategorieId, pageable)
                    .map(mapper::toDTO);
        }

        if (categorieId != null) {
            return repository.findBySousCategorieCategorieId(categorieId, pageable)
                    .map(mapper::toDTO);
        }

        return repository.findAll(pageable).map(mapper::toDTO);
    }

    /* ================= DELETE ================= */

    public void delete(Long id) {

        FormationContinues f = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Formation", "id", id)
                );

        f.setEnabled(false);
        repository.save(f);
    }

    /* ================= UTIL ================= */

    private Integer generateReference() {

        Integer ref;

        do {
            ref = 5000 + random.nextInt(2000); // 🔥 5000 → 6999
        } while (repository.findByReference(ref) != null);

        return ref;
    }
}