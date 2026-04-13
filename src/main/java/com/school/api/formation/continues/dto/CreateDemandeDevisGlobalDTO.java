package com.school.api.formation.continues.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;
@Data
public class CreateDemandeDevisGlobalDTO {

    @NotEmpty
    private List<DemandeDevisItemDTO> formations;

    @NotBlank
    private String nomClient;

    @Email
    private String email;

    @NotBlank
    private String telephone;

    private boolean entreprise;
    private String nomStructure;

    @Data
    public static class DemandeDevisItemDTO {

        @NotBlank
        private String slug;

        @Min(1)
        private int participants;
    }
}