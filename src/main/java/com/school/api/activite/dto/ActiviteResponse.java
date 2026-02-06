package com.school.api.activite.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiviteResponse {

  private Long id;
  private String titre;
  private String contenu;
  private List<ActiviteImageResponse> images;
}
