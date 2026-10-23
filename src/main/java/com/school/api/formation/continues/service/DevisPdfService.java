package com.school.api.formation.continues.service;

import com.school.api.common.storage.StoredFileResolver;
import com.school.api.formation.continues.entity.DemandeDevisFormationContinues;
import com.school.api.formation.continues.entity.DevisFormationContinues;
import com.school.api.formation.continues.entity.TypeReduction;
import com.school.api.formation.continues.service.pdf.DevisLignePdf;
import com.school.api.formation.continues.service.pdf.MontantEnLettres;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DevisPdfService {

    private static final String TEMPLATE = "/reports/devis.jrxml";
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final StoredFileResolver fileResolver;

    @Value("${app.devis.ecole.decrets:}")          private String ecoleDecrets;
    @Value("${app.devis.ecole.adresse:}")          private String ecoleAdresse;
    @Value("${app.devis.ecole.email:}")            private String ecoleEmail;
    @Value("${app.devis.ecole.site:}")             private String ecoleSite;
    @Value("${app.devis.logo:}")                   private String logo;
    @Value("${app.devis.objet:Formation}")         private String objet;
    @Value("${app.devis.devise:XAF}")              private String devise;
    @Value("${app.devis.paiement.beneficiaire:}")  private String paiementBeneficiaire;
    @Value("${app.devis.paiement.mode:}")          private String paiementMode;
    @Value("${app.devis.paiement.banque:}")        private String paiementBanque;
    @Value("${app.devis.paiement.rib:}")           private String paiementRib;

    /* Template compilé une seule fois */
    private volatile JasperReport report;

    /* =========================
       GÉNÉRATION
       ========================= */
    public byte[] generer(DevisFormationContinues devis) {

        InputStream logoStream = fileResolver.open(logo);
        InputStream signatureStream = fileResolver.open(devis.getEmetteurSignatureUrl());
        InputStream cachetStream = fileResolver.open(devis.getEmetteurCachetUrl());

        try {
            Map<String, Object> params = parametres(devis);
            params.put("LOGO", logoStream);
            params.put("SIGNATURE", signatureStream);
            params.put("CACHET", cachetStream);

            /* Une ligne du tableau par formation */
            List<DevisLignePdf> lignes = devis.getLignes().stream()
                    .map(l -> new DevisLignePdf(
                            l.getLibelle(),
                            String.valueOf(l.getNombreParticipants()),
                            montant(l.getPrixUnitaire()),
                            montant(l.getMontant())))
                    .toList();

            JasperPrint print = JasperFillManager.fillReport(
                    getReport(), params, new JRBeanCollectionDataSource(lignes));

            return JasperExportManager.exportReportToPdf(print);

        } catch (JRException e) {
            throw new RuntimeException("Erreur lors de la génération du devis PDF", e);
        } finally {
            fermer(logoStream);
            fermer(signatureStream);
            fermer(cachetStream);
        }
    }

    /* =========================
       PARAMÈTRES DU TEMPLATE
       ========================= */
    private Map<String, Object> parametres(DevisFormationContinues devis) {

        DemandeDevisFormationContinues d = devis.getDemande();
        Map<String, Object> p = new HashMap<>();

        /* École */
        p.put("ECOLE_DECRETS", ecoleDecrets);
        p.put("ECOLE_ADRESSE", ecoleAdresse);
        p.put("ECOLE_EMAIL", ecoleEmail);
        p.put("ECOLE_SITE", ecoleSite);

        /* Devis */
        p.put("NUMERO", devis.getNumero());
        p.put("DATE_EMISSION", devis.getDateEmission().format(DATE));
        p.put("DATE_VALIDITE", devis.getDateValidite().format(DATE));
        p.put("OBJET", objet);

        /* Client */
        boolean avecStructure = d.isEntreprise() && !vide(d.getNomStructure());
        p.put("CLIENT_NOM", avecStructure ? d.getNomStructure() : d.getNomClient());
        p.put("CLIENT_ATTENTION", avecStructure ? "À l'attention de " + d.getNomClient() : null);
        p.put("CLIENT_TEL", vide(d.getTelephone()) ? null : d.getTelephone());
        p.put("CLIENT_EMAIL", vide(d.getEmail()) ? null : d.getEmail());

        /* Totaux : Total → Réduction (facultative) → Net à payer */
        boolean avecReduction = devis.getMontantReduction() != null && devis.getMontantReduction() > 0;

        p.put("TOTAL", montant(devis.getMontantTotal()));
        p.put("AVEC_REDUCTION", avecReduction);
        p.put("REDUCTION_LIBELLE",
                devis.getTypeReduction() == TypeReduction.TAUX
                        ? "Réduction (" + pourcent(devis.getValeurReduction()) + ")"
                        : "Réduction");
        p.put("REDUCTION_MONTANT", montant(devis.getMontantReduction()));
        p.put("NET_A_PAYER", montant(devis.getNetAPayer()));
        p.put("MONTANT_LETTRES", MontantEnLettres.convertir(
                Math.round(devis.getNetAPayer() == null ? 0 : devis.getNetAPayer())));

        /* Paiement */
        p.put("PAIEMENT_BENEFICIAIRE", paiementBeneficiaire);
        p.put("PAIEMENT_MODE", paiementMode);
        p.put("PAIEMENT_BANQUE", paiementBanque);
        p.put("PAIEMENT_RIB", paiementRib);
        p.put("CONDITIONS", devis.getConditions());

        /* Validateur (snapshot du devis) */
        p.put("EMETTEUR_NOM", devis.getEmetteurNom());
        p.put("EMETTEUR_FONCTION", devis.getEmetteurFonction());

        return p;
    }

    /* =========================
       COMPILATION (une seule fois)
       ========================= */
    private JasperReport getReport() throws JRException {
        JasperReport r = report;
        if (r == null) {
            synchronized (this) {
                r = report;
                if (r == null) {
                    try (InputStream in = getClass().getResourceAsStream(TEMPLATE)) {
                        if (in == null) throw new JRException("Template introuvable : " + TEMPLATE);
                        r = JasperCompileManager.compileReport(in);
                        report = r;
                    } catch (IOException e) {
                        throw new JRException("Lecture du template impossible", e);
                    }
                }
            }
        }
        return r;
    }

    private void fermer(InputStream in) {
        if (in == null) return;
        try { in.close(); } catch (IOException ignored) { }
    }

    /* =========================
       FORMATAGE : 460.000 XAF / 10%
       ========================= */
    private String montant(Double valeur) {
        DecimalFormatSymbols s = new DecimalFormatSymbols(Locale.FRANCE);
        s.setGroupingSeparator('.');
        return new DecimalFormat("#,##0", s).format(valeur == null ? 0 : valeur) + " " + devise;
    }

    private String pourcent(Double valeur) {
        return new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.FRANCE))
                .format(valeur == null ? 0 : valeur) + "%";
    }

    private static boolean vide(String s) {
        return s == null || s.isBlank();
    }
}