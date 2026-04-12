package com.school.api.formation.continues.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateDemandeDevisGlobalDTO {

    @NotBlank
    private String nomClient;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String telephone;

    private boolean entreprise;

    private String nomStructure;

    @NotEmpty
    private List<LigneDemandeDTO> formations;

    @Getter
    @Setter
    public static class LigneDemandeDTO {

        @NotBlank
        private String slug;

        @Min(1)
        private int participants;
    }
}