package com.school.api.formation.continues.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LigneDemandeDTO {

    @NotNull
    private Long formationId;

    @NotNull
    private Integer nombreParticipants;
}