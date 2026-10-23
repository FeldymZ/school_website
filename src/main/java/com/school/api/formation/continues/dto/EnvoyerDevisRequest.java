package com.school.api.formation.continues.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnvoyerDevisRequest {

    @Size(max = 4000)
    private String message; // null → message par défaut
}