package com.school.api.formation.continues.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DemandeDevisReponseDTO {

    private Long id;
    private String message;
    private String pieceJointeUrl;
    private String envoyePar;
    private LocalDateTime dateEnvoi;
}