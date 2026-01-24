package com.school.api.formation.preinscription.dto;

import com.school.api.formation.initiale.entity.FormationInitialeLevel;
import com.school.api.formation.preinscription.entity.enums.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record FormationPreinscriptionRequest(

  /* ===== INFOS PERSONNELLES ===== */
  @NotBlank String nom,
  @NotBlank String prenom,
  @NotNull LocalDate dateNaissance,
  @NotBlank String lieuNaissance,
  @NotNull Sexe sexe,
  @NotBlank String nationalite,
  @NotBlank String adresse,
  @NotBlank String telephone,
  @Email @NotBlank String email,
  @NotNull SituationFamiliale situationFamiliale,

  /* ===== ÉTABLISSEMENT ===== */
  @NotBlank String nomEtablissement,
  @NotNull TypeEtablissement typeEtablissement,
  @NotNull SerieBaccalaureat serieBaccalaureat,
  @NotNull Integer anneeObtention,

  /* ===== FORMATION ===== */
  @NotNull Long formationId,
  @NotNull FormationInitialeLevel niveau,
  @NotNull Integer niveauEtude,

  /* ===== STATUT ===== */
  @NotNull StatutEtudiant statutEtudiant,
  @NotNull ModeFinancement modeFinancement,
  String autreFinancement,

  /* ===== PARENT ===== */
  @NotBlank String profession
) {}
