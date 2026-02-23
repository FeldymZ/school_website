package com.school.api.formation.continues.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateFormationContinuesDTO {

  @NotBlank(message = "Le titre est obligatoire")
  private String titre;

  @NotBlank(message = "La description est obligatoire")
  private String description;

  private MultipartFile cover;
  private MultipartFile pdf;
}
