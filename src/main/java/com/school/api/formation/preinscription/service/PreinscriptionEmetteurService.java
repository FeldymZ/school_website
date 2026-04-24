package com.school.api.formation.preinscription.service;

import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.common.storage.FileStorageService;
import com.school.api.formation.preinscription.dto.PreinscriptionEmetteurResponse;
import com.school.api.formation.preinscription.entity.PreinscriptionEmetteur;
import com.school.api.formation.preinscription.repository.PreinscriptionEmetteurRepository;
import com.school.api.formation.preinscription.repository.PreinscriptionPeriodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreinscriptionEmetteurService {

    private final PreinscriptionEmetteurRepository emetteurRepo;
    private final PreinscriptionPeriodeRepository periodeRepo;
    private final FileStorageService fileStorageService;

    /* ================= GET ALL ================= */
    @Transactional(readOnly = true)
    public List<PreinscriptionEmetteurResponse> getAll() {

        return emetteurRepo.findAllOrdered()
                .stream()
                .map(e -> PreinscriptionEmetteurResponse.builder()
                        .id(e.getId())
                        .nom(e.getNom())
                        .fonction(e.getFonction())
                        .signatureUrl(e.getSignatureUrl())
                        .actif(e.isActif())
                        .nbPeriodes(periodeRepo.countByEmetteur_Id(e.getId()))
                        .build()
                )
                .toList();
    }

    /* ================= CREATE ================= */
    @Transactional
    public void create(String nom, String fonction, MultipartFile signature) {

        if (signature == null || signature.isEmpty()) {
            throw new IllegalArgumentException("Signature obligatoire");
        }

        String path = fileStorageService.storeSignature(signature);

        emetteurRepo.save(
                PreinscriptionEmetteur.builder()
                        .nom(nom.trim())
                        .fonction(fonction.trim())
                        .signatureUrl(path)
                        .actif(false)
                        .build()
        );
    }

    /* ================= UPDATE ================= */
    @Transactional
    public void update(Long id, String nom, String fonction, MultipartFile signature) {

        PreinscriptionEmetteur em = emetteurRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emetteur", "id", id));

        em.setNom(nom.trim());
        em.setFonction(fonction.trim());

        if (signature != null && !signature.isEmpty()) {
            String path = fileStorageService.storeSignature(signature);
            em.setSignatureUrl(path);
        }
    }

    /* ================= DELETE ================= */
    @Transactional
    public void delete(Long id) {

        PreinscriptionEmetteur em = emetteurRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emetteur", "id", id));

        if (em.isActif()) {
            throw new IllegalStateException("Impossible de supprimer un émetteur actif");
        }

        if (periodeRepo.existsByEmetteur_Id(id)) {
            throw new IllegalStateException("Émetteur utilisé dans une période");
        }

        emetteurRepo.delete(em);
    }

    /* ================= ACTIVATE ================= */
    @Transactional
    public void activate(Long id) {

        PreinscriptionEmetteur em = emetteurRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emetteur", "id", id));

        emetteurRepo.deactivateAll();
        em.setActif(true);
    }

    /* ================= ACTIVE ================= */
    @Transactional(readOnly = true)
    public PreinscriptionEmetteurResponse getActive() {

        PreinscriptionEmetteur e = emetteurRepo.findByActifTrue().orElse(null);

        if (e == null) return null;

        return PreinscriptionEmetteurResponse.builder()
                .id(e.getId())
                .nom(e.getNom())
                .fonction(e.getFonction())
                .signatureUrl(e.getSignatureUrl())
                .actif(true)
                .nbPeriodes(periodeRepo.countByEmetteur_Id(e.getId()))
                .build();
    }
}