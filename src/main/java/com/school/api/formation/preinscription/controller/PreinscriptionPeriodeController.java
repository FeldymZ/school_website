package com.school.api.formation.preinscription.controller;

import com.school.api.formation.preinscription.entity.PreinscriptionPeriode;
import com.school.api.formation.preinscription.service.PreinscriptionPeriodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/preinscriptions/periodes")
@RequiredArgsConstructor
public class PreinscriptionPeriodeController {

    private final PreinscriptionPeriodeService service;

    /* ================= GET ALL ================= */
    @GetMapping
    public List<PreinscriptionPeriode> getAll() {
        return service.getAll();
    }

    /* ================= DELETE ================= */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deletePeriode(id); // ✅ CORRIGÉ
    }
}