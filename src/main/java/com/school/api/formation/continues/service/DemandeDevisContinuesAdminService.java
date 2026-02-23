package com.school.api.formation.continues.service;

import com.school.api.common.mail.MailService;
import com.school.api.common.storage.FileStorageService;
import com.school.api.formation.continues.dto.RepondreDemandeDevisContinuesDTO;
import com.school.api.formation.continues.entity.*;
import com.school.api.formation.continues.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DemandeDevisContinuesAdminService {

  private final DemandeDevisFormationContinuesRepository demandeRepository;
  private final DemandeDevisReponseContinuesRepository reponseRepository;
  private final MailService mailService;
  private final FileStorageService fileStorageService;

  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public void repondre(Long id, RepondreDemandeDevisContinuesDTO dto) {

    var demande = demandeRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Demande introuvable"));

    String fileUrl = null;

    if (dto.getPieceJointe() != null && !dto.getPieceJointe().isEmpty()) {
      fileUrl = fileStorageService
        .storeDevisContinuesAttachment(dto.getPieceJointe());
    }

    mailService.sendDevisResponse(
      demande.getEmail(),
      demande.getNomClient(),
      dto.getMessage(),
      fileUrl
    );

    DemandeDevisReponseContinues reponse =
      new DemandeDevisReponseContinues();

    reponse.setMessage(dto.getMessage());
    reponse.setPieceJointeUrl(fileUrl);
    reponse.setEnvoyePar("ADMIN");
    reponse.setDateEnvoi(LocalDateTime.now());
    reponse.setDemande(demande);

    reponseRepository.save(reponse);
  }

  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public void marquerTraitee(Long id) {

    var demande = demandeRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Demande introuvable"));

    demande.setStatut(StatutDemande.TRAITEE);
    demande.setDateTraitement(LocalDateTime.now());

    demandeRepository.save(demande);
  }

  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public Page<DemandeDevisFormationContinues> getDemandes(
    String statut,
    int page,
    int size
  ) {

    Pageable pageable = PageRequest.of(
      page,
      size,
      Sort.by(Sort.Direction.DESC, "dateDemande")
    );

    if (statut != null && !statut.isBlank()) {

      StatutDemande statutEnum =
        StatutDemande.valueOf(statut.toUpperCase());

      return demandeRepository.findByStatut(
        statutEnum,
        pageable
      );
    }

    return demandeRepository.findAll(pageable);
  }

  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public long countNonTraitees() {
    return demandeRepository.countByStatut(
      StatutDemande.PAS_ENCORE_TRAITEE
    );
  }
}
