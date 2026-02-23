package com.school.api.formation.continues.service;

import com.school.api.formation.continues.dto.CreateDemandeDevisContinuesDTO;
import com.school.api.formation.continues.entity.DemandeDevisFormationContinues;
import com.school.api.formation.continues.entity.FormationContinues;
import com.school.api.formation.continues.repository.DemandeDevisFormationContinuesRepository;
import com.school.api.formation.continues.repository.FormationContinuesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DemandeDevisContinuesPublicService {

  private final DemandeDevisFormationContinuesRepository repository;
  private final FormationContinuesRepository formationRepository;

  public void create(Long formationId, CreateDemandeDevisContinuesDTO dto) {

    FormationContinues formation = formationRepository.findById(formationId)
      .orElseThrow(() -> new RuntimeException("Formation introuvable"));

    DemandeDevisFormationContinues demande =
      new DemandeDevisFormationContinues();

    demande.setNomClient(dto.getNomClient());
    demande.setEmail(dto.getEmail());
    demande.setTelephone(dto.getTelephone());
    demande.setEntreprise(dto.isEntreprise());
    demande.setNomStructure(dto.getNomStructure());
    demande.setNombreParticipants(dto.getNombreParticipants());
    demande.setDureeSouhaitee(dto.getDureeSouhaitee());
    demande.setDateDemande(LocalDate.now());
    demande.setFormation(formation);

    repository.save(demande);
  }
}
