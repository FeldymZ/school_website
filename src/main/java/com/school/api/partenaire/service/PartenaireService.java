package com.school.api.partenaire.service;

import com.school.api.common.storage.FileStorageService;
import com.school.api.partenaire.entity.Partenaire;
import com.school.api.partenaire.repository.PartenaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartenaireService {

  private final PartenaireRepository repository;
  private final FileStorageService fileStorageService;

  /* ============================
     🌍 PUBLIC
     ============================ */

  public List<Partenaire> getPublic() {
    return repository.findByEnabledTrueOrderByDisplayOrderAsc();
  }

  /* ============================
     🔐 ADMIN
     ============================ */

  public List<Partenaire> getAll() {
    return repository.findAllByOrderByDisplayOrderAsc();
  }

  public Partenaire create(
    String name,
    String websiteUrl,
    Integer displayOrder,
    Boolean enabled,
    MultipartFile logo
  ) {

    if (repository.existsByDisplayOrder(displayOrder)) {
      throw new IllegalArgumentException("Ordre déjà utilisé");
    }

    if (logo == null || logo.isEmpty()) {
      throw new IllegalArgumentException("Logo obligatoire");
    }

    String logoUrl = fileStorageService.storePartenaireLogo(logo);

    Partenaire partenaire = Partenaire.builder()
      .name(name)
      .websiteUrl(websiteUrl)
      .displayOrder(displayOrder)
      .enabled(enabled != null ? enabled : true)
      .logoUrl(logoUrl)
      .build();

    return repository.save(partenaire);
  }

  public Partenaire update(
    Long id,
    String name,
    String websiteUrl,
    Integer displayOrder,
    Boolean enabled
  ) {

    Partenaire p = repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Partenaire introuvable"));

    if (displayOrder != null &&
      repository.existsByDisplayOrderAndIdNot(displayOrder, id)) {
      throw new IllegalArgumentException("Ordre déjà utilisé");
    }

    if (name != null) p.setName(name);
    if (websiteUrl != null) p.setWebsiteUrl(websiteUrl);
    if (displayOrder != null) p.setDisplayOrder(displayOrder);
    if (enabled != null) p.setEnabled(enabled);

    return repository.save(p);
  }

  public Partenaire updateLogo(Long id, MultipartFile logo) {

    if (logo == null || logo.isEmpty()) {
      throw new IllegalArgumentException("Logo manquant");
    }

    Partenaire p = repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Partenaire introuvable"));

    String logoUrl = fileStorageService.storePartenaireLogo(logo);
    p.setLogoUrl(logoUrl);

    return repository.save(p);
  }

  public void delete(Long id) {
    repository.deleteById(id);
  }

  public void reorder(List<Long> orderedIds) {

    if (orderedIds == null || orderedIds.isEmpty()) {
      throw new IllegalArgumentException("Liste d’IDs vide");
    }

    int order = 1;

    for (Long id : orderedIds) {

      Partenaire partenaire = repository.findById(id)
        .orElseThrow(() ->
          new RuntimeException("Partenaire introuvable : " + id)
        );

      partenaire.setDisplayOrder(order++);
      repository.save(partenaire);
    }
  }


}
