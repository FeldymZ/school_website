package com.school.api.formation.preinscription.service;

import com.school.api.formation.preinscription.entity.PreinscriptionDemande;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class PreinscriptionJasperService {

    private static final DateTimeFormatter DATE_FR =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ⚠️ Chemin absolu du logo sur le serveur (partagé via Docker/Nginx)
    private static final String LOGO_PATH =
            "/files/assets/logos/esiitech.png";

    public byte[] generatePdf(PreinscriptionDemande demande) {
        try {
            InputStream template = new ClassPathResource(
                    "reports/preinscription.jrxml"
            ).getInputStream();

            JasperReport report   = JasperCompileManager.compileReport(template);
            JRDataSource  emptyDs = new JRBeanCollectionDataSource(List.of());
            JasperPrint   print   = JasperFillManager.fillReport(
                    report, buildParams(demande), emptyDs
            );

            return JasperExportManager.exportReportToPdf(print);

        } catch (Exception e) {
            log.error("❌ Erreur génération PDF préinscription", e);
            throw new RuntimeException("Erreur génération PDF préinscription", e);
        }
    }

    private Map<String, Object> buildParams(PreinscriptionDemande d) {

        var periode   = d.getPeriode();
        var session   = periode.getSession();
        var emetteur  = periode.getEmetteur();
        var formation = d.getFormation();

        Map<String, Object> p = new HashMap<>();

        p.put("LOGO_PATH",         LOGO_PATH);
        p.put("NUMERO_DEMANDE",    genererNumero(d.getId()));
        p.put("ANNEE_UNIV",        session.getAnnee());

        p.put("CIVILITE",          d.getCivilite().getLabel());
        p.put("NOM",               d.getNom().toUpperCase());
        p.put("PRENOM",            d.getPrenom());
        p.put("DATE_NAISSANCE",    d.getDateNaissance().format(DATE_FR));
        p.put("LIEU_NAISSANCE",    d.getLieuNaissance());
        p.put("NATIONALITE",       d.getNationalite());
        p.put("EMAIL",             d.getEmail());
        p.put("TELEPHONE",         d.getTelephone());
        p.put("WHATSAPP",          d.getWhatsapp() != null ? d.getWhatsapp() : "—");

        p.put("NIVEAU",            d.getNiveauSouhaite().getLabel());
        p.put("FORMATION_TITRE",
                formation.getLevel().getLabel() + " " + formation.getName());
        p.put("SPECIALITE",        formation.getName());

        p.put("EMETTEUR_NOM",      emetteur.getNom());
        p.put("EMETTEUR_FONCTION", emetteur.getFonction());
        p.put("SIGNATURE_PATH",    emetteur.getSignatureUrl());

        p.put("DATE_EMISSION",
                d.getValidatedAt().toLocalDate().format(DATE_FR));

        return p;
    }

    /**
     * Génère un numéro de demande à partir de 1000.
     * Ex: id=1  → "1001"
     *     id=42 → "1042"
     */
    private String genererNumero(Long id) {
        return String.valueOf(1000 + id);
    }
}