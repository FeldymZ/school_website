package com.school.api.formation.preinscription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejetPreinscriptionRequest(

        @NotBlank(message = "Le motif du rejet est obligatoire")
        @Size(min = 10, max = 1000,
                message = "Le motif doit contenir entre 10 et 1000 caractères")
        String motif
) {}