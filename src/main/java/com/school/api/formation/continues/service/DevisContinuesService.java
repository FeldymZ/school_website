package com.school.api.formation.continues.service;

import com.school.api.formation.continues.dto.DevisDTO;
import com.school.api.formation.continues.dto.GenererDevisRequest;
import com.school.api.formation.continues.entity.*;
import com.school.api.formation.continues.mapper.DemandeDevisMapper;
import com.school.api.formation.continues.repository.DemandeDevisFormationContinuesRepository;
import com.school.api.formation.continues.repository.DevisEmetteurRepository;
import com.school.api.formation.continues.repository.DevisFormationContinuesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevisContinuesService {

    private final DemandeDevisFormationContinuesRepository demandeRepository;
    private final DevisFormationContinuesRepository devisRepository;
    private final DevisEmetteurRepository emetteurRepository;
    private final DevisPdfService pdfService;
    private final DemandeDevisMapper mapper;

    @Value("${app.devis.validite-jours:30}")
    private int validiteDefaut;

    public record DevisPdf(String nomFichier, byte[] contenu) {}

    /* =========================
       ✅ VALIDER (GÉNÉRER / RÉGÉNÉRER)
       ========================= */
    @Transactional
    public DevisDTO generer(Long demandeId, GenererDevisRequest req, String admin) {

        DemandeDevisFormationContinues demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));

        if (demande.getStatut() == StatutDemande.FERMEE) {
            throw new RuntimeException("Demande clôturée : impossible de valider un devis");
        }
        if (demande.getLignes() == null || demande.getLignes().isEmpty()) {
            throw new RuntimeException("La demande ne contient aucune formation");
        }

        /* Validateur : celui choisi, sinon le validateur par défaut */
        DevisEmetteur validateur = (req.getEmetteurId() != null)
                ? emetteurRepository.findById(req.getEmetteurId())
                .orElseThrow(() -> new RuntimeException("Validateur introuvable"))
                : emetteurRepository.findByActifTrue()
                .orElseThrow(() -> new RuntimeException(
                        "Aucun validateur sélectionné. Choisissez un validateur pour valider le devis."));

        /* Prix / participants modifiés par l'admin, indexés par id de ligne */
        Map<Long, GenererDevisRequest.LigneOverride> overrides =
                (req.getLignes() == null ? List.<GenererDevisRequest.LigneOverride>of() : req.getLignes())
                        .stream()
                        .collect(Collectors.toMap(
                                GenererDevisRequest.LigneOverride::getLigneId,
                                Function.identity(),
                                (a, b) -> b));

        /* Devis existant → mis à jour en gardant son numéro */
        DevisFormationContinues devis = demande.getDevis();
        if (devis == null) {
            devis = new DevisFormationContinues();
            devis.setNumero(prochainNumero());
            devis.setDemande(demande);
            demande.setDevis(devis);
        }

        devis.getLignes().clear();

        /* Une ligne de devis par formation demandée */
        int ordre = 1;
        double total = 0;

        for (DemandeDevisLigneFormationContinues l : demande.getLignes()) {

            GenererDevisRequest.LigneOverride ov = overrides.get(l.getId());

            int participants = (ov != null && ov.getNombreParticipants() != null)
                    ? ov.getNombreParticipants()
                    : (l.getNombreParticipants() != null ? l.getNombreParticipants() : 1);

            double prix = (ov != null && ov.getPrixUnitaire() != null)
                    ? ov.getPrixUnitaire()
                    : (l.getPrix() != null ? l.getPrix() : 0);

            /* Prix par participant */
            double montant = arrondi(prix * participants);

            DevisLigneFormationContinues dl = new DevisLigneFormationContinues();
            dl.setOrdre(ordre++);
            dl.setLigneDemandeId(l.getId());
            dl.setLibelle(l.getFormationLibelle());
            dl.setDuree(l.getDuree());
            dl.setUniteDuree(l.getUniteDuree() != null ? l.getUniteDuree().name() : null);
            dl.setNombreParticipants(participants);
            dl.setPrixUnitaire(prix);
            dl.setMontant(montant);
            dl.setDevis(devis);

            devis.getLignes().add(dl);
            total += montant;
        }

        /* ================= RÉDUCTION ================= */
        TypeReduction type = req.getTypeReduction() != null ? req.getTypeReduction() : TypeReduction.AUCUNE;
        Double valeur = req.getValeurReduction();
        double reduction = calculerReduction(total, type, valeur);

        devis.setMontantTotal(total);
        devis.setTypeReduction(type);
        devis.setValeurReduction(type == TypeReduction.AUCUNE ? null : valeur);
        devis.setMontantReduction(reduction);
        devis.setNetAPayer(total - reduction);

        /* ================= INFOS ================= */
        int validite = req.getValiditeJours() != null ? req.getValiditeJours() : validiteDefaut;

        devis.setDateEmission(LocalDate.now());
        devis.setDateValidite(LocalDate.now().plusDays(validite));
        devis.setConditions(req.getConditions() != null && !req.getConditions().isBlank()
                ? req.getConditions().trim() : null);
        devis.setGenerePar(admin);
        devis.setDateGeneration(LocalDateTime.now());
        devis.setDateEnvoi(null); // nouvelle version, pas encore envoyée

        /* ================= VALIDATEUR (snapshot) ================= */
        devis.setEmetteur(validateur);
        devis.setEmetteurNom(validateur.getNom());
        devis.setEmetteurFonction(validateur.getFonction());
        devis.setEmetteurSignatureUrl(validateur.getSignatureUrl());
        devis.setEmetteurCachetUrl(validateur.getCachetUrl());

        devisRepository.save(devis);

        if (demande.getStatut() == StatutDemande.PAS_ENCORE_TRAITEE) {
            demande.setStatut(StatutDemande.EN_COURS);
        }
        demande.setDateTraitement(LocalDateTime.now());
        demandeRepository.save(demande);

        return mapper.toDevisDTO(devis);
    }

    /* =========================
       🔍 DEVIS D'UNE DEMANDE
       ========================= */
    @Transactional(readOnly = true)
    public Optional<DevisDTO> getByDemande(Long demandeId) {
        return demandeRepository.findById(demandeId)
                .map(DemandeDevisFormationContinues::getDevis)
                .map(mapper::toDevisDTO);
    }

    /* =========================
       📄 PDF
       ========================= */
    @Transactional(readOnly = true)
    public DevisPdf pdf(Long demandeId) {

        DevisFormationContinues devis = demandeRepository.findById(demandeId)
                .map(DemandeDevisFormationContinues::getDevis)
                .orElseThrow(() -> new RuntimeException("Aucun devis validé pour cette demande"));

        return new DevisPdf(devis.getNumero() + ".pdf", pdfService.generer(devis));
    }

    /* =========================
       💸 CALCUL DE LA RÉDUCTION
       ========================= */
    private double calculerReduction(double total, TypeReduction type, Double valeur) {
        return switch (type) {
            case AUCUNE -> 0;
            case TAUX -> {
                if (valeur == null || valeur <= 0 || valeur > 100) {
                    throw new RuntimeException("Le taux de réduction doit être compris entre 0 et 100 %");
                }
                yield arrondi(total * valeur / 100);
            }
            case MONTANT -> {
                if (valeur == null || valeur <= 0) {
                    throw new RuntimeException("Le montant de la réduction doit être supérieur à 0");
                }
                if (valeur > total) {
                    throw new RuntimeException("La réduction ne peut pas dépasser le total du devis");
                }
                yield arrondi(valeur);
            }
        };
    }

    /* =========================
       🔢 NUMÉROTATION : DEV-2026-0001
       ========================= */
    private synchronized String prochainNumero() {

        String prefix = "DEV-" + Year.now().getValue() + "-";

        int suivant = devisRepository
                .findTopByNumeroStartingWithOrderByNumeroDesc(prefix)
                .map(d -> Integer.parseInt(d.getNumero().substring(prefix.length())) + 1)
                .orElse(1);

        return String.format("%s%04d", prefix, suivant);
    }

    /* XAF : pas de centimes */
    private static double arrondi(double v) {
        return Math.round(v);
    }
}