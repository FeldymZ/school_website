package com.school.api.formation.preinscription.dto;

import com.school.api.formation.initiale.entity.FormationInitialeLevel;
import com.school.api.formation.preinscription.entity.enums.StatutPreinscription;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record FormationPreinscriptionAdminResponse(

  Long id,

  // Identité
  String nom,
  String prenom,
  LocalDate dateNaissance,
  String email,
  String telephone,

  // Formation
  Long formationId,
  String formationNom,
  FormationInitialeLevel niveau,

  // Workflow
  StatutPreinscription statut,
  LocalDateTime createdAt,
  LocalDateTime decisionAt
) {}
