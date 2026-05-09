package com.school.api.formation.preinscription.controller;

import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.formation.preinscription.entity.PreinscriptionDemande;
import com.school.api.formation.preinscription.repository.PreinscriptionDemandeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/preinscriptions")
public class PreinscriptionPdfController {

    private static final String BASE_DIR = "/files";

    private final PreinscriptionDemandeRepository demandeRepo;

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> getPdf(
            @PathVariable Long id
    ) {

        PreinscriptionDemande demande =
                demandeRepo.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Demande",
                                        "id",
                                        id
                                )
                        );

        if (demande.getPdfUrl() == null) {

            throw new IllegalStateException(
                    "Aucun PDF disponible pour cette demande"
            );
        }

        /* ================= URL → CHEMIN DISQUE ================= */

        String relativePath =
                demande.getPdfUrl()
                        .replace("/files/", "");

        File file = new File(
                BASE_DIR,
                relativePath
        );

        if (!file.exists()) {

            throw new IllegalStateException(
                    "Fichier PDF introuvable : "
                            + file.getAbsolutePath()
            );
        }

        Resource resource =
                new FileSystemResource(file);

        return ResponseEntity.ok()

                .contentType(MediaType.APPLICATION_PDF)

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\""
                                + file.getName()
                                + "\""
                )

                .body(resource);
    }
}