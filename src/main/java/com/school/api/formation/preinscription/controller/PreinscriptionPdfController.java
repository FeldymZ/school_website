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
@RequestMapping("/api/admin/preinscriptions")
public class PreinscriptionPdfController {

    private final PreinscriptionDemandeRepository demandeRepo;

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> getPdf(
            @PathVariable Long id
    ) {

        System.out.println("=================================");
        System.out.println("✅ PDF ENDPOINT HIT");
        System.out.println("ID = " + id);
        System.out.println("=================================");

        /* ================= DEMANDE ================= */

        PreinscriptionDemande demande =
                demandeRepo.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Demande",
                                        "id",
                                        id
                                )
                        );

        System.out.println("DEMANDE TROUVEE");
        System.out.println("PDF URL = " + demande.getPdfUrl());

        /* ================= PDF URL ================= */

        if (demande.getPdfUrl() == null) {

            System.out.println("❌ PDF URL NULL");

            throw new IllegalStateException(
                    "Aucun PDF disponible pour cette demande"
            );
        }

        /* ================= PATH ABSOLU ================= */

        String relativePath =
                demande.getPdfUrl()
                        .replace("/files/", "");

        System.out.println("RELATIVE PATH = " + relativePath);

        File file = new File(
                "/files/" + relativePath
        );

        System.out.println("ABSOLUTE PATH = " + file.getAbsolutePath());
        System.out.println("FILE EXISTS = " + file.exists());
        System.out.println("IS FILE = " + file.isFile());
        System.out.println("CAN READ = " + file.canRead());

        /* ================= FILE CHECK ================= */

        if (!file.exists()) {

            System.out.println("❌ FICHIER INTROUVABLE");

            throw new IllegalStateException(
                    "Fichier PDF introuvable : "
                            + file.getAbsolutePath()
            );
        }

        /* ================= RESOURCE ================= */

        Resource resource =
                new FileSystemResource(file);

        System.out.println("✅ PDF ENVOYE");

        return ResponseEntity.ok()

                .contentType(MediaType.APPLICATION_PDF)

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=preinscription.pdf"
                )

                .body(resource);
    }
}