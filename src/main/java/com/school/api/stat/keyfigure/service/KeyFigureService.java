package com.school.api.stat.keyfigure.service;

import com.school.api.stat.keyfigure.dto.KeyFigureOrderRequest;
import com.school.api.stat.keyfigure.dto.KeyFigureRequest;
import com.school.api.stat.keyfigure.dto.KeyFigureResponse;
import com.school.api.stat.keyfigure.entity.KeyFigure;
import com.school.api.stat.keyfigure.repository.KeyFigureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeyFigureService {

  private final KeyFigureRepository repository;

  /* ============================
     🌍 PUBLIC
     ============================ */

  public List<KeyFigureResponse> getPublic() {
    return repository.findByEnabledTrueOrderByDisplayOrderAsc()
      .stream()
      .map(this::toDto)
      .toList();
  }

  /* ============================
     🔐 ADMIN
     ============================ */

  public List<KeyFigureResponse> getAll() {
    return repository.findAllByOrderByDisplayOrderAsc()
      .stream()
      .map(this::toDto)
      .toList();
  }

  public KeyFigureResponse create(KeyFigureRequest request) {

    KeyFigure keyFigure = KeyFigure.builder()
      .label(request.label())
      .value(request.value())
      .displayOrder(request.displayOrder())
      .enabled(request.enabled() != null ? request.enabled() : true)
      .build();

    return toDto(repository.save(keyFigure));
  }

  public KeyFigureResponse update(Long id, KeyFigureRequest request) {

    KeyFigure keyFigure = get(id);

    if (request.label() != null) {
      keyFigure.setLabel(request.label());
    }

    if (request.value() != null) {
      keyFigure.setValue(request.value());
    }

    if (request.displayOrder() != null) {
      keyFigure.setDisplayOrder(request.displayOrder());
    }

    if (request.enabled() != null) {
      keyFigure.setEnabled(request.enabled());
    }

    return toDto(repository.save(keyFigure));
  }

  public void delete(Long id) {
    repository.delete(get(id));
  }

  /* ============================
     🧩 UTILS
     ============================ */

  private KeyFigure get(Long id) {
    return repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Chiffre clé introuvable"));
  }

  private KeyFigureResponse toDto(KeyFigure k) {
    return KeyFigureResponse.builder()
      .id(k.getId())
      .label(k.getLabel())
      .value(k.getValue())
      .displayOrder(k.getDisplayOrder())
      .enabled(k.getEnabled())
      .build();
  }


  /* ============================
   🔀 REORDER
   ============================ */

  @Transactional
  public void reorder(List<KeyFigureOrderRequest> orders) {

    long distinctCount = orders.stream()
      .map(KeyFigureOrderRequest::displayOrder)
      .distinct()
      .count();

    if (distinctCount != orders.size()) {
      throw new IllegalArgumentException(
        "Deux chiffres clés ne peuvent pas avoir le même ordre"
      );
    }

    for (KeyFigureOrderRequest item : orders) {
      KeyFigure keyFigure = get(item.id());
      keyFigure.setDisplayOrder(item.displayOrder());
    }
  }

}
