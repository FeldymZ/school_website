package com.school.api.formation.preinscription.service;

import com.school.api.common.mail.MailService;
import com.school.api.formation.initiale.entity.FormationInitiale;
import com.school.api.formation.initiale.repository.FormationInitialeRepository;
import com.school.api.formation.preinscription.dto.FormationPreinscriptionAdminResponse;
import com.school.api.formation.preinscription.dto.FormationPreinscriptionRequest;
import com.school.api.formation.preinscription.entity.FormationPreinscription;
import com.school.api.formation.preinscription.entity.enums.StatutPreinscription;
import com.school.api.formation.preinscription.repository.FormationPreinscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FormationPreinscriptionService {

  private final FormationInitialeRepository formationRepository;
  private final FormationPreinscriptionRepository repository;
  private final MailService mailService;

  /* =========================
     PUBLIC : PRÉINSCRIPTION
     ========================= */

  public void preinscrire(FormationPreinscriptionRequest request) {

    FormationInitiale formation = formationRepository.findById(request.formationId())
      .orElseThrow(() -> new RuntimeException("Formation introuvable"));

    FormationPreinscription p = FormationPreinscription.builder()
      .nom(request.nom())
      .prenom(request.prenom())
      .dateNaissance(request.dateNaissance())
      .lieuNaissance(request.lieuNaissance())
      .sexe(request.sexe())
      .nationalite(request.nationalite())
      .adresse(request.adresse())
      .telephone(request.telephone())
      .email(request.email())
      .situationFamiliale(request.situationFamiliale())
      .nomEtablissement(request.nomEtablissement())
      .typeEtablissement(request.typeEtablissement())
      .serieBaccalaureat(request.serieBaccalaureat())
      .anneeObtention(request.anneeObtention())
      .formation(formation)
      .niveau(request.niveau())
      .niveauEtude(request.niveauEtude())
      .statutEtudiant(request.statutEtudiant())
      .modeFinancement(request.modeFinancement())
      .autreFinancement(request.autreFinancement())
      .profession(request.profession())
      .statut(StatutPreinscription.EN_ATTENTE)
      .createdAt(LocalDateTime.now())
      .build();

    repository.save(p);

    File fiche = generatePreinscriptionWord(request, formation);
    sendConfirmationEmail(p, formation, fiche);
  }

  /* =========================
     ADMIN : LISTES (DTO)
     ========================= */

  public List<FormationPreinscriptionAdminResponse> getAll() {
    return repository.findAll()
      .stream()
      .map(this::toAdminResponse)
      .toList();
  }

  public List<FormationPreinscriptionAdminResponse> getByStatut(
    StatutPreinscription statut
  ) {
    return repository.findByStatut(statut)
      .stream()
      .map(this::toAdminResponse)
      .toList();
  }

  /* =========================
     ADMIN : DÉCISION
     ========================= */

  public void decide(Long id, boolean accepted, String commentaire) {

    FormationPreinscription p = repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Préinscription introuvable"));

    if (p.getStatut() != StatutPreinscription.EN_ATTENTE) {
      throw new IllegalStateException("Décision déjà prise");
    }

    p.setStatut(
      accepted
        ? StatutPreinscription.VALIDEE
        : StatutPreinscription.REFUSEE
    );

    p.setCommentaireAdmin(commentaire);
    p.setDecisionAt(LocalDateTime.now());

    repository.save(p);

    sendDecisionEmail(p, accepted);
  }

  /* =========================
     ADMIN : WORD
     ========================= */

  public File downloadWord(Long id) {

    FormationPreinscription p = repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Préinscription introuvable"));

    if (p.getStatut() == StatutPreinscription.EN_ATTENTE) {
      throw new IllegalStateException("Téléchargement non autorisé");
    }

    FormationInitiale formation = p.getFormation();

    FormationPreinscriptionRequest r =
      new FormationPreinscriptionRequest(
        p.getNom(),
        p.getPrenom(),
        p.getDateNaissance(),
        p.getLieuNaissance(),
        p.getSexe(),
        p.getNationalite(),
        p.getAdresse(),
        p.getTelephone(),
        p.getEmail(),
        p.getSituationFamiliale(),
        p.getNomEtablissement(),
        p.getTypeEtablissement(),
        p.getSerieBaccalaureat(),
        p.getAnneeObtention(),
        formation.getId(),
        p.getNiveau(),
        p.getNiveauEtude(),
        p.getStatutEtudiant(),
        p.getModeFinancement(),
        p.getAutreFinancement(),
        p.getProfession()
      );

    return generatePreinscriptionWord(r, formation);
  }

  /* =========================
     MAPPER ADMIN
     ========================= */

  private FormationPreinscriptionAdminResponse toAdminResponse(
    FormationPreinscription p
  ) {
    return new FormationPreinscriptionAdminResponse(
      p.getId(),
      p.getNom(),
      p.getPrenom(),
      p.getDateNaissance(),
      p.getEmail(),
      p.getTelephone(),
      p.getFormation().getId(),
      p.getFormation().getName(),
      p.getNiveau(),
      p.getStatut(),
      p.getCreatedAt(),
      p.getDecisionAt()
    );
  }

  /* =========================
     WORD + EMAILS
     ========================= */

  private File generatePreinscriptionWord(
    FormationPreinscriptionRequest r,
    FormationInitiale formation
  ) {
    try {
      ClassPathResource template =
        new ClassPathResource("templates/preinscription/fiche-preinscription-master.docx");

      XWPFDocument document =
        new XWPFDocument(template.getInputStream());

      Map<String, String> values = new HashMap<>();
      values.put("nom", r.nom());
      values.put("prenom", r.prenom());
      values.put("formation", formation.getName());

      for (XWPFParagraph p : document.getParagraphs()) {
        for (XWPFRun run : p.getRuns()) {
          String text = run.getText(0);
          if (text == null) continue;

          for (Map.Entry<String, String> e : values.entrySet()) {
            text = text.replace("{{" + e.getKey() + "}}", e.getValue());
          }
          run.setText(text, 0);
        }
      }

      File output = File.createTempFile(
        "preinscription-" + r.nom() + "-" + r.prenom(),
        ".docx"
      );

      try (FileOutputStream fos = new FileOutputStream(output)) {
        document.write(fos);
      }

      return output;

    } catch (Exception e) {
      throw new RuntimeException("Erreur génération fiche de préinscription", e);
    }
  }

  private void sendConfirmationEmail(
    FormationPreinscription p,
    FormationInitiale formation,
    File fiche
  ) {
    try {
      ByteArrayResource attachment =
        new ByteArrayResource(Files.readAllBytes(fiche.toPath()));

      mailService.sendHtmlWithAttachment(
        p.getEmail(),
        "Préinscription enregistrée – ESIITECH",
        "<p>Bonjour <strong>%s %s</strong>,</p>"
          .formatted(p.getPrenom(), p.getNom()),
        fiche.getName(),
        attachment
      );

    } catch (Exception e) {
      throw new RuntimeException("Erreur envoi email candidat", e);
    }
  }

  private void sendDecisionEmail(FormationPreinscription p, boolean accepted) {

    mailService.sendHtml(
      p.getEmail(),
      accepted
        ? "Préinscription validée – ESIITECH"
        : "Préinscription refusée – ESIITECH",
      "<p>Bonjour <strong>%s %s</strong>,</p>"
        .formatted(p.getPrenom(), p.getNom())
    );
  }
}
