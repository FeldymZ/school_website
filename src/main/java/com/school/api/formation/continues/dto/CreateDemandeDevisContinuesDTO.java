package com.school.api.formation.continues.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDemandeDevisContinuesDTO {

  @NotBlank(message = "Le nom est obligatoire")
  private String nomClient;

  @NotBlank(message = "L'email est obligatoire")
  @Email(message = "Email invalide")
  private String email;

  @NotBlank(message = "Le téléphone est obligatoire")
  private String telephone;

  private boolean entreprise;

  private String nomStructure;

  @NotNull(message = "Le nombre de participants est obligatoire")
  private Integer nombreParticipants;

  @NotNull(message = "La durée souhaitée est obligatoire")
  private Integer dureeSouhaitee;
}
