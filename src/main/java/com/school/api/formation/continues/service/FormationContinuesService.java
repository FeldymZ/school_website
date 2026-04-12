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
        f.setSlug(generateSlug(dto.getLibelle())); // 🔥

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
            f.setLogo(fileStorageService.storeFormationContinuesCover(dto.getCover()));
        }

        return mapper.toDTO(repository.save(f));
    }

    /* ================= GET PUBLIC ================= */

    public Page<FormationDTO> getAllPublic(int page, int size) {
        return repository.findByEnabledTrue(
                PageRequest.of(page, size, Sort.by("id").descending())
        ).map(mapper::toDTO);
    }

    public FormationDTO getBySlug(String slug) {

        FormationContinues f = repository.findBySlug(slug);

        if (f == null || !f.isEnabled()) {
            throw new ResourceNotFoundException("Formation", "slug", slug);
        }

        return mapper.toDTO(f);
    }

    /* ================= UTIL ================= */

    private String generateSlug(String libelle) {
        return libelle
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    private Integer generateReference() {

        Integer ref;

        do {
            ref = 5000 + random.nextInt(2000);
        } while (repository.findByReference(ref) != null);

        return ref;
    }
}