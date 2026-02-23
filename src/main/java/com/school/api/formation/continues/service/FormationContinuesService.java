package com.school.api.formation.continues.service;

import com.school.api.common.storage.FileStorageService;
import com.school.api.formation.continues.dto.CreateFormationContinuesDTO;
import com.school.api.formation.continues.entity.FormationContinues;
import com.school.api.formation.continues.repository.FormationContinuesRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FormationContinuesService {

  private final FormationContinuesRepository repository;
  private final FileStorageService fileStorageService;

  /* =====================================================
     🟢 CRÉATION (ADMIN)
     ===================================================== */

  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public FormationContinues create(@Valid CreateFormationContinuesDTO dto) {

    FormationContinues formation = new FormationContinues();

    formation.setTitre(dto.getTitre());
    formation.setDescription(dto.getDescription());
    formation.setSlug(generateSlug(dto.getTitre()));
    formation.setEnabled(true);

    // Cover
    if (dto.getCover() != null && !dto.getCover().isEmpty()) {
      formation.setCoverUrl(
        fileStorageService.storeFormationContinuesCover(dto.getCover())
      );
    }

    // PDF
    if (dto.getPdf() != null && !dto.getPdf().isEmpty()) {
      formation.setPdfUrl(
        fileStorageService.storeFormationContinuesPdf(dto.getPdf())
      );
    }

    return repository.save(formation);
  }

  /* =====================================================
     🟡 MISE À JOUR (ADMIN)
     ===================================================== */

  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public FormationContinues update(Long id, @Valid CreateFormationContinuesDTO dto) {

    FormationContinues formation = repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Formation introuvable"));

    // Si le titre change réellement → on régénère le slug
    if (!formation.getTitre().equals(dto.getTitre())) {
      formation.setSlug(generateSlug(dto.getTitre()));
    }

    formation.setTitre(dto.getTitre());
    formation.setDescription(dto.getDescription());

    // Cover
    if (dto.getCover() != null && !dto.getCover().isEmpty()) {

      if (formation.getCoverUrl() != null) {
        fileStorageService.deleteQuietly(formation.getCoverUrl());
      }

      formation.setCoverUrl(
        fileStorageService.storeFormationContinuesCover(dto.getCover())
      );
    }

    // PDF
    if (dto.getPdf() != null && !dto.getPdf().isEmpty()) {

      if (formation.getPdfUrl() != null) {
        fileStorageService.deleteQuietly(formation.getPdfUrl());
      }

      formation.setPdfUrl(
        fileStorageService.storeFormationContinuesPdf(dto.getPdf())
      );
    }

    return repository.save(formation);
  }

  /* =====================================================
     🔵 LISTE ADMIN PAGINÉE
     ===================================================== */

  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public Page<FormationContinues> getAll(int page, int size) {

    Pageable pageable = PageRequest.of(
      page,
      size,
      Sort.by(Sort.Direction.DESC, "id")
    );

    return repository.findAll(pageable);
  }

  /* =====================================================
     🟢 LISTE PUBLIQUE (ENABLED)
     ===================================================== */

  public Page<FormationContinues> getAllPublic(int page, int size) {

    Pageable pageable = PageRequest.of(
      page,
      size,
      Sort.by(Sort.Direction.DESC, "id")
    );

    return repository.findByEnabledTrue(pageable);
  }

  /* =====================================================
     🔍 DÉTAIL
     ===================================================== */

  public FormationContinues getBySlug(String slug) {

    return repository.findBySlug(slug)
      .filter(FormationContinues::isEnabled)
      .orElseThrow(() -> new RuntimeException("Formation introuvable"));
  }

  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public FormationContinues getById(Long id) {

    return repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Formation introuvable"));
  }

  /* =====================================================
     🔁 ACTIVER / DÉSACTIVER
     ===================================================== */

  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public void toggle(Long id) {

    FormationContinues formation = repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Formation introuvable"));

    formation.setEnabled(!formation.isEnabled());

    repository.save(formation);
  }

  /* =====================================================
     ❌ SUPPRESSION
     ===================================================== */

  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public void delete(Long id) {

    FormationContinues formation = repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Formation introuvable"));

    if (formation.getCoverUrl() != null) {
      fileStorageService.deleteQuietly(formation.getCoverUrl());
    }

    if (formation.getPdfUrl() != null) {
      fileStorageService.deleteQuietly(formation.getPdfUrl());
    }

    repository.delete(formation);
  }

  /* =====================================================
     🧠 SLUG AUTO
     ===================================================== */

  private String generateSlug(String titre) {

    String base = StringUtils
      .replace(titre.toLowerCase(), " ", "-")
      .replaceAll("[^a-z0-9-]", "");

    return base + "-" + UUID.randomUUID().toString().substring(0, 6);
  }
}
