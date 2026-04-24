package com.school.api.formation.preinscription.service;

import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.common.storage.FileStorageService;
import com.school.api.formation.preinscription.entity.PreinscriptionEmetteur;
import com.school.api.formation.preinscription.repository.PreinscriptionEmetteurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreinscriptionEmetteurService {

    private final PreinscriptionEmetteurRepository emetteurRepo;
    private final FileStorageService fileStorageService;

    /* ================= GET ================= */
    public List<PreinscriptionEmetteur> getAll() {
        return emetteurRepo.findAll();
    }

    /* ================= CREATE ================= */
    @Transactional
    public void create(String nom, String fonction, MultipartFile signature) {

        String path = fileStorageService.storeSignature(signature);

        emetteurRepo.save(
                PreinscriptionEmetteur.builder()
                        .nom(nom)
                        .fonction(fonction)
                        .signatureUrl(path)
                        .actif(false)
                        .build()
        );
    }

    /* ================= ACTIVATE ================= */
    @Transactional
    public void activate(Long id) {

        // désactiver tous
        emetteurRepo.findAll().forEach(e -> e.setActif(false));

        PreinscriptionEmetteur em = emetteurRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emetteur", "id", id));

        em.setActif(true);
        emetteurRepo.save(em);
    }

    /* ================= GET ACTIVE ================= */
    public PreinscriptionEmetteur getActive() {
        return emetteurRepo.findAll()
                .stream()
                .filter(PreinscriptionEmetteur::isActif)
                .findFirst()
                .orElse(null);
    }
}