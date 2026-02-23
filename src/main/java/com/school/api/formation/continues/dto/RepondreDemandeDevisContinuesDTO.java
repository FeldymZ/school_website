package com.school.api.formation.continues.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class RepondreDemandeDevisContinuesDTO {

  private String message;

  private MultipartFile pieceJointe;
}
