package com.school.api.formation.preinscription.service;

import com.school.api.common.exception.ResourceNotFoundException;
import com.school.api.common.mail.MailService;
import com.school.api.common.storage.FileStorageService;
import com.school.api.formation.initiale.entity.FormationInitiale;
import com.school.api.formation.initiale.repository.FormationInitialeRepository;
import com.school.api.formation.preinscription.dto.*;
import com.school.api.formation.preinscription.entity.*;
import com.school.api.formation.preinscription.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreinscriptionService {

    private final PreinscriptionDemandeRepository demandeRepo;
    private final PreinscriptionPeriodeRepository periodeRepo;
    private final FormationInitialeRepository formationRepo;
    private final PreinscriptionEmetteurRepository emetteurRepo;
    private final SessionUniversitaireRepository sessionRepo;

    private final MailService mailService;
    private final PreinscriptionJasperService jasperService;
    private final FileStorageService fileStorageService;

    private LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of("Africa/Libreville"));
    }

    /* ================= VALIDATION DIPLOME ================= */

    private void validateDiplome(PreinscriptionDemandeRequest req) {

        if (req.statutDiplome() == StatutDiplome.OBTENU) {

            if (req.anneeObtention() == null) {
                throw new IllegalArgumentException(
                        "L'année d'obtention est obligatoire pour un diplôme obtenu"
                );
            }

        } else if (req.statutDiplome() == StatutDiplome.EN_COURS) {

            if (req.anneeObtention() != null) {
                throw new IllegalArgumentException(
                        "Ne renseignez pas l'année d'obtention si le diplôme est en cours"
                );
            }
        }
    }

    /* ================= PUBLIC ================= */

    @Transactional
    public PreinscriptionDemandeResponse submit(PreinscriptionDemandeRequest req) {

        PreinscriptionPeriode periode = getActivePeriode();

        if (periode == null) {
            throw new IllegalStateException("Les préinscriptions sont fermées.");
        }

        FormationInitiale formation = formationRepo.findById(req.formationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Formation",
                        "id",
                        req.formationId()
                ));

        /* 🔥 VALIDATION METIER */
        validateDiplome(req);

        PreinscriptionDemande demande = PreinscriptionDemande.builder()
                .civilite(req.civilite())
                .nom(req.nom())
                .prenom(req.prenom())
                .dateNaissance(req.dateNaissance())
                .lieuNaissance(req.lieuNaissance())
                .nationalite(req.nationalite())
                .email(req.email())
                .telephone(req.telephone())
                .whatsapp(req.whatsapp())
                .niveauSouhaite(req.niveauSouhaite())

                /* 🔥 DIPLOME */
                .diplomePresente(req.diplomePresente())
                .statutDiplome(req.statutDiplome())
                .anneeObtention(req.anneeObtention())
                .etablissementProvenance(req.etablissementProvenance())

                .formation(formation)
                .periode(periode)
                .statut(StatutDemande.EN_ATTENTE)
                .build();

        PreinscriptionDemande saved = demandeRepo.save(demande);

        mailService.sendPreinscriptionRecue(
                saved.getEmail(),
                saved.getCivilite().getLabel(),
                saved.getNom(),
                formation.getLevel().getLabel() + " " + formation.getName(),
                saved.getNiveauSouhaite().getLabel(),
                periode.getSession().getAnnee()
        );

        return toDto(saved);
    }

    /* ================= ADMIN ================= */

    public List<PreinscriptionDemandeResponse> getAll() {
        return demandeRepo.findAllWithRelations()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<PreinscriptionDemandeResponse> getByStatut(StatutDemande statut) {
        return demandeRepo.findByStatutWithRelations(statut)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<PreinscriptionDemandeResponse> getByFormation(Long formationId) {
        return demandeRepo.findByFormationWithRelations(formationId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public PreinscriptionDemandeResponse getById(Long id) {
        return demandeRepo.findByIdWithRelations(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Demande",
                        "id",
                        id
                ));
    }

    /* ================= VALIDATION ================= */

    @Transactional
    public PreinscriptionDemandeResponse validate(Long id) {

        PreinscriptionDemande d = demandeRepo.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Demande",
                        "id",
                        id
                ));

        d.setStatut(StatutDemande.VALIDEE);
        d.setValidatedAt(now());

        byte[] pdf = jasperService.generatePdf(d);

        String filename = "preinscription_" + d.getId() + ".pdf";

        String path = fileStorageService.storePreinscriptionPdf(
                pdf,
                filename
        );

        d.setPdfUrl(path);

        mailService.sendPreinscriptionValidee(
                d.getEmail(),
                d.getCivilite().getLabel(),
                d.getNom(),
                d.getFormation().getLevel().getLabel() + " " + d.getFormation().getName(),
                d.getNiveauSouhaite().getLabel(),
                d.getPeriode().getSession().getAnnee(),
                path
        );

        return toDto(demandeRepo.save(d));
    }

    @Transactional
    public void reject(Long id) {

        PreinscriptionDemande d = demandeRepo.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Demande",
                        "id",
                        id
                ));

        d.setStatut(StatutDemande.REJETEE);
    }

    /* ================= AUTRES ================= */

    @Transactional
    public void deletePeriode(Long id) {

        PreinscriptionPeriode periode = periodeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PreinscriptionPeriode",
                        "id",
                        id
                ));

        if (periode.isActive()) {
            throw new IllegalStateException(
                    "Désactivez la période avant suppression"
            );
        }

        periodeRepo.delete(periode);
    }

    @Transactional
    public void deleteSession(Long id) {

        SessionUniversitaire session = sessionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SessionUniversitaire",
                        "id",
                        id
                ));

        if (periodeRepo.existsBySession_Id(id)) {
            throw new IllegalStateException(
                    "Impossible de supprimer une session contenant des périodes"
            );
        }

        sessionRepo.delete(session);
    }

    public List<PreinscriptionEmetteur> getAllEmetteurs() {
        return emetteurRepo.findAll();
    }

    @Transactional
    public void createEmetteur(
            String nom,
            String fonction,
            MultipartFile signature
    ) {

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

    @Transactional
    public void activateEmetteur(Long id) {

        emetteurRepo.findAll().forEach(e -> e.setActif(false));

        PreinscriptionEmetteur em = emetteurRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Emetteur",
                        "id",
                        id
                ));

        em.setActif(true);
    }

    public SessionPublicResponse getActiveSession() {

        PreinscriptionPeriode p = getActivePeriode();

        if (p == null) {
            return SessionPublicResponse.builder()
                    .ouverte(false)
                    .anneeUniversitaire(null)
                    .dateDebut(null)
                    .dateFin(null)
                    .build();
        }

        return SessionPublicResponse.builder()
                .ouverte(true)
                .anneeUniversitaire(p.getSession().getAnnee())
                .dateDebut(p.getDateDebut())
                .dateFin(p.getDateFin())
                .build();
    }

    public boolean isSessionOuverte() {
        return getActivePeriode() != null;
    }

    private PreinscriptionPeriode getActivePeriode() {

        LocalDateTime now = now();

        return periodeRepo
                .findFirstByActiveTrueAndDateDebutBeforeAndDateFinAfterOrderByDateDebutDesc(
                        now,
                        now
                )
                .orElse(null);
    }

    private PreinscriptionDemandeResponse toDto(
            PreinscriptionDemande d
    ) {

        return PreinscriptionDemandeResponse.builder()

                .id(d.getId())

                /* ================= IDENTITE ================= */

                .civilite(d.getCivilite().getLabel())

                .nom(d.getNom())
                .prenom(d.getPrenom())

                .dateNaissance(
                        d.getDateNaissance() != null
                                ? d.getDateNaissance().toString()
                                : null
                )

                .lieuNaissance(d.getLieuNaissance())

                .nationalite(d.getNationalite())

                /* ================= CONTACT ================= */

                .email(d.getEmail())

                .telephone(d.getTelephone())

                .whatsapp(d.getWhatsapp())

                /* ================= FORMATION ================= */

                .niveau(
                        d.getNiveauSouhaite().getLabel()
                )

                .formation(
                        d.getFormation().getName()
                )

                /* ================= DIPLOME ================= */

                .diplomePresente(
                        d.getDiplomePresente()
                )

                .statutDiplome(
                        d.getStatutDiplome() != null
                                ? d.getStatutDiplome().name()
                                : null
                )

                .anneeObtention(
                        d.getAnneeObtention()
                )

                .etablissementProvenance(
                        d.getEtablissementProvenance()
                )

                /* ================= AUTRES ================= */

                .anneeUniversitaire(
                        d.getPeriode().getSession().getAnnee()
                )

                .statut(
                        d.getStatut()
                )

                .createdAt(
                        d.getCreatedAt()
                )

                .validatedAt(
                        d.getValidatedAt()
                )

                .pdfUrl(
                        d.getPdfUrl()
                )

                .build();
    }
}