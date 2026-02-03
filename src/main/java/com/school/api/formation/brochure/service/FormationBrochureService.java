package com.school.api.formation.brochure.service;

import com.school.api.common.mail.MailService;
import com.school.api.common.storage.PublicUrlResolver;
import com.school.api.formation.brochure.dto.FormationBrochureRequestDto;
import com.school.api.formation.brochure.entity.FormationBrochureRequest;
import com.school.api.formation.brochure.repository.FormationBrochureRequestRepository;
import com.school.api.formation.initiale.entity.FormationInitiale;
import com.school.api.formation.initiale.repository.FormationInitialeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FormationBrochureService {

  private final FormationInitialeRepository formationRepository;
  private final FormationBrochureRequestRepository brochureRepository;
  private final MailService mailService;
  private final PublicUrlResolver publicUrlResolver;

  /* =====================================================
     PUBLIC — SLUG
     ===================================================== */

  @Transactional
  public void sendBrochureBySlug(
    String slug,
    FormationBrochureRequestDto request
  ) {
    FormationInitiale formation = formationRepository
      .findBySlug(slug)
      .filter(FormationInitiale::getEnabled)
      .orElseThrow(() ->
        new RuntimeException("Formation introuvable ou indisponible")
      );

    sendBrochureInternal(formation, request);
  }

  /* =====================================================
     ADMIN / INTERNE — ID
     ===================================================== */

  @Transactional
  public void sendBrochure(
    Long formationId,
    FormationBrochureRequestDto request
  ) {
    FormationInitiale formation = formationRepository.findById(formationId)
      .orElseThrow(() -> new RuntimeException("Formation introuvable"));

    sendBrochureInternal(formation, request);
  }

  /* =====================================================
     LOGIQUE COMMUNE
     ===================================================== */

  private void sendBrochureInternal(
    FormationInitiale formation,
    FormationBrochureRequestDto request
  ) {

    if (formation.getPdfUrl() == null || formation.getPdfUrl().isBlank()) {
      throw new IllegalStateException(
        "Aucune maquette disponible pour cette formation"
      );
    }

    // Sauvegarde de la demande
    brochureRepository.save(
      FormationBrochureRequest.builder()
        .studentName(request.name())
        .email(request.email())
        .formation(formation)
        .requestedAt(LocalDateTime.now())
        .build()
    );

    // URL publique du PDF
    String pdfPublicUrl =
      publicUrlResolver.toAbsoluteUrl(formation.getPdfUrl());

    // Envoi email
    mailService.sendFormationBrochure(
      request.email(),
      request.name(),
      formation.getName(),
      pdfPublicUrl
    );
  }
}
