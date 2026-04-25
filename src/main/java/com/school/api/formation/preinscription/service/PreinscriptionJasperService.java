package com.school.api.formation.preinscription.service;

import com.school.api.formation.preinscription.entity.PreinscriptionDemande;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.JREmptyDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PreinscriptionJasperService {

    private static final DateTimeFormatter DATE_FR =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String LOGO_PATH = "/app/assets/logo.png";

    public byte[] generatePdf(PreinscriptionDemande demande) {
        try {
            InputStream template = new ClassPathResource(
                    "reports/preinscription.jrxml"
            ).getInputStream();

            JasperReport report = JasperCompileManager.compileReport(template);

            JasperPrint print = JasperFillManager.fillReport(
                    report,
                    buildParams(demande),
                    new JREmptyDataSource(1)
            );

            return JasperExportManager.exportReportToPdf(print);

        } catch (Exception e) {
            log.error(" Erreur génération PDF préinscription", e);
            throw new RuntimeException("Erreur génération PDF préinscription", e);
        }
    }

    private Map<String, Object> buildParams(PreinscriptionDemande d) {

        var periode   = d.getPeriode();
        var session   = periode.getSession();
        var emetteur  = periode.getEmetteur();
        var formation = d.getFormation();

        Map<String, Object> p = new HashMap<>();

        /* ================= LOGO ================= */
        try {
            InputStream logoStream = new FileInputStream(LOGO_PATH);
            p.put("LOGO", logoStream);
        } catch (Exception e) {
            log.warn(" Logo non trouvé : {}", LOGO_PATH);
            p.put("LOGO", null); // 🔥 évite crash Jasper
        }

        /* ================= INFOS ================= */
        p.put("NUMERO_DEMANDE", genererNumero(d.getId()));
        p.put("ANNEE_UNIV", session.getAnnee());

        p.put("CIVILITE", d.getCivilite().getLabel());
        p.put("NOM", d.getNom().toUpperCase());
        p.put("PRENOM", d.getPrenom());
        p.put("DATE_NAISSANCE", d.getDateNaissance().format(DATE_FR));
        p.put("LIEU_NAISSANCE", d.getLieuNaissance());
        p.put("NATIONALITE", d.getNationalite());
        p.put("EMAIL", d.getEmail());
        p.put("TELEPHONE", d.getTelephone());
        p.put("WHATSAPP", d.getWhatsapp() != null ? d.getWhatsapp() : "—");

        /* ================= FORMATION ================= */
        p.put("NIVEAU", d.getNiveauSouhaite().getLabel());

        String formationComplete =
                formation.getLevel().getLabel() + " en " + formation.getName();

        p.put("FORMATION_TITRE", formationComplete);
        p.put("SPECIALITE", formation.getName());

        /* ================= EMETTEUR ================= */
        p.put("EMETTEUR_NOM", emetteur.getNom());
        p.put("EMETTEUR_FONCTION", emetteur.getFonction());

        /* 🔥 SIGNATURE SÉCURISÉE */
        try {
            if (emetteur.getSignatureUrl() != null) {
                InputStream signatureStream =
                        new FileInputStream(emetteur.getSignatureUrl());
                p.put("SIGNATURE", signatureStream);
            } else {
                p.put("SIGNATURE", null);
            }
        } catch (Exception e) {
            log.warn(" Signature non trouvée : {}", emetteur.getSignatureUrl());
            p.put("SIGNATURE", null);
        }

        /* ================= DATE ================= */
        p.put("DATE_EMISSION",
                d.getValidatedAt().toLocalDate().format(DATE_FR));

        return p;
    }

    private String genererNumero(Long id) {
        return String.valueOf(1000 + id);
    }
}