package com.school.api.vieetudiante.visiteentreprise.controller;

import com.school.api.vieetudiante.visiteentreprise.entity.VisiteEntreprise;
import com.school.api.vieetudiante.visiteentreprise.service.VisiteEntrepriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/vie-etudiante/visites-entreprise")
public class VisiteEntrepriseAdminController {

    private final VisiteEntrepriseService service;

    /* =========================
       CREATE
       ========================= */
    @PostMapping(consumes = "multipart/form-data")
    public VisiteEntreprise create(
            @RequestParam String titre,
            @RequestParam String contenu,
            @RequestParam(required = false) MultipartFile[] photos,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime datePublication,
            @RequestParam boolean published
    ) {
        return service.create(
                titre,
                contenu,
                photos,
                datePublication,
                published
        );
    }

    /* =========================
       READ (ADMIN – TOUS)
       ========================= */
    @GetMapping
    public List<VisiteEntreprise> getAll() {
        return service.getAllAdmin();
    }

    /* =========================
       READ (ADMIN – PAR ID)
       ========================= */
    @GetMapping("/{id}")
    public VisiteEntreprise getById(@PathVariable Long id) {
        return service.getByIdAdmin(id);
    }

    /* =========================
       UPDATE
       ========================= */
    @PutMapping(
            value = "/{id}",
            consumes = "multipart/form-data"
    )
    public VisiteEntreprise update(
            @PathVariable Long id,
            @RequestParam String titre,
            @RequestParam String contenu,
            @RequestParam(required = false) MultipartFile[] photos,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime datePublication,
            @RequestParam boolean published
    ) {
        return service.update(
                id,
                titre,
                contenu,
                photos,
                datePublication,
                published
        );
    }

    /* =========================
       DELETE
       ========================= */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
