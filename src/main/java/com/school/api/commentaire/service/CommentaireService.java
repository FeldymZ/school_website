package com.school.api.commentaire.service;

import com.school.api.commentaire.dto.*;
import com.school.api.commentaire.entity.Commentaire;
import com.school.api.commentaire.repository.CommentaireRepository;
import com.school.api.common.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentaireService {

  private final CommentaireRepository repository;
  private final FileStorageService fileStorageService;

  /* ============================
     🌍 PUBLIC
     ============================ */

  /**
   * Commentaires visibles côté public (carousel)
   */
  public List<CommentaireResponse> getPublic() {
    return repository
      .findByEnabledTrueOrderByDisplayOrderAsc()
      .stream()
      .map(this::toDto)
      .toList();
  }

  /* ============================
     🔐 ADMIN
     ============================ */

  /**
   * Liste complète admin
   */
  public List<CommentaireResponse> getAll() {
    return repository
      .findAllByOrderByDisplayOrderAsc()
      .stream()
      .map(this::toDto)
      .toList();
  }

  /**
   * Création d’un commentaire / témoignage
   */
  public CommentaireResponse create(
    CommentaireCreateRequest request,
    MultipartFile authorImage
  ) {

    if (request.displayOrder() == null) {
      throw new IllegalArgumentException("L’ordre d’affichage est obligatoire");
    }

    // ❗ ordre unique
    if (repository.existsByDisplayOrder(request.displayOrder())) {
      throw new IllegalArgumentException(
        "Un commentaire avec cet ordre existe déjà"
      );
    }

    if (authorImage == null || authorImage.isEmpty()) {
      throw new IllegalArgumentException(
        "L’image (photo) de l’auteur est obligatoire"
      );
    }

    String imageUrl = fileStorageService.storeCommentaireAvatar(authorImage);

    Commentaire commentaire = Commentaire.builder()
      .authorName(request.authorName())
      .content(request.content())
      .displayDate(request.displayDate())
      .displayOrder(request.displayOrder())
      .enabled(request.enabled() != null ? request.enabled() : true)
      .authorImageUrl(imageUrl)
      .build();

    return toDto(repository.save(commentaire));
  }

  /**
   * Mise à jour d’un commentaire
   */
  public CommentaireResponse update(
    Long id,
    CommentaireUpdateRequest request
  ) {

    Commentaire c = repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Commentaire introuvable"));

    // ❗ contrôle unicité de l’ordre (hors soi-même)
    if (request.displayOrder() != null &&
      repository.existsByDisplayOrderAndIdNot(request.displayOrder(), id)) {
      throw new IllegalArgumentException(
        "Un commentaire avec cet ordre existe déjà"
      );
    }

    if (request.authorName() != null) {
      c.setAuthorName(request.authorName());
    }

    if (request.content() != null) {
      c.setContent(request.content());
    }

    if (request.displayDate() != null) {
      c.setDisplayDate(request.displayDate());
    }

    if (request.displayOrder() != null) {
      c.setDisplayOrder(request.displayOrder());
    }

    if (request.enabled() != null) {
      c.setEnabled(request.enabled());
    }

    return toDto(repository.save(c));
  }

  /**
   * Suppression
   */
  public void delete(Long id) {
    repository.deleteById(id);
  }

  /* ============================
     🧩 UTILS
     ============================ */

  private CommentaireResponse toDto(Commentaire c) {
    return CommentaireResponse.builder()
      .id(c.getId())
      .authorName(c.getAuthorName())
      .content(c.getContent())
      .displayDate(c.getDisplayDate())
      .authorImageUrl(c.getAuthorImageUrl())
      .build();
  }

  public void reorder(CommentaireReorderRequest request) {

    List<Long> ids = request.orderedIds();

    if (ids == null || ids.isEmpty()) {
      throw new IllegalArgumentException("Liste d’IDs vide");
    }

    int order = 1; // on commence à 1 (plus lisible côté admin)

    for (Long id : ids) {

      Commentaire commentaire = repository.findById(id)
        .orElseThrow(() ->
          new RuntimeException("Commentaire introuvable : " + id)
        );

      commentaire.setDisplayOrder(order++);
      repository.save(commentaire);
    }
  }

}
