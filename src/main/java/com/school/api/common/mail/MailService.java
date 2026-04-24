package com.school.api.common.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.*;
import org.springframework.mail.javamail.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Year;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender mailSender;
  private final SpringTemplateEngine templateEngine;
  private final MailProperties mailProperties;

  /* =====================================================
     🔹 EMAIL HTML SIMPLE
     ===================================================== */
  @Async
  public void sendHtml(String to, String subject, String htmlContent) {
    try {
      MimeMessage message = mailSender.createMimeMessage();

      MimeMessageHelper helper =
              new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());

      helper.setFrom(mailProperties.getFrom());
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      mailSender.send(message);

      log.info("📧 Email HTML envoyé à {}", to);

    } catch (Exception e) {
      log.error("❌ Erreur envoi email HTML", e);
    }
  }

  /* =====================================================
     🔹 EMAIL TEMPLATE THYMELEAF
     ===================================================== */
  @Async
  public void sendTemplateMail(
          String to,
          String subject,
          String template,
          Context context
  ) {
    try {
      String html = templateEngine.process(template, context);
      sendHtml(to, subject, html);
    } catch (Exception e) {
      log.error("❌ Erreur email template", e);
    }
  }

  /* =====================================================
     🔹 EMAIL AVEC PJ (MultipartFile)
     ===================================================== */
  @Async
  public void sendHtmlWithAttachment(
          String to,
          String subject,
          String htmlContent,
          MultipartFile file
  ) {
    try {

      MimeMessage message = mailSender.createMimeMessage();

      MimeMessageHelper helper =
              new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

      helper.setFrom(mailProperties.getFrom());
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      helper.addAttachment(
              file.getOriginalFilename(),
              new ByteArrayResource(file.getBytes())
      );

      mailSender.send(message);

      log.info("📎 Email avec PJ envoyé à {}", to);

    } catch (Exception e) {
      log.error("❌ Erreur email PJ", e);
    }
  }

  /* =====================================================
     🔹 EMAIL AVEC PJ (FICHIER DISQUE)
     ===================================================== */
  @Async
  public void sendHtmlWithAttachmentFromPath(
          String to,
          String subject,
          String htmlContent,
          String filePath
  ) {
    try {

      MimeMessage message = mailSender.createMimeMessage();

      MimeMessageHelper helper =
              new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

      helper.setFrom(mailProperties.getFrom());
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      Path path = Path.of(filePath);

      if (Files.exists(path)) {
        helper.addAttachment(
                path.getFileName().toString(),
                new FileSystemResource(path.toFile())
        );
      } else {
        log.warn("⚠️ Fichier introuvable : {}", filePath);
      }

      mailSender.send(message);

      log.info("📎 Email (fichier disque) envoyé à {}", to);

    } catch (Exception e) {
      log.error("❌ Erreur email fichier disque", e);
    }
  }

  /* =====================================================
     🔹 CONFIRMATION DEMANDE (CLIENT)
     ===================================================== */
  @Async
  public void sendDemandeConfirmation(
          String to,
          String clientName
  ) {
    try {

      Context context = new Context();
      context.setVariable("name", clientName);
      context.setVariable("year", Year.now().getValue());

      String html = templateEngine.process("mail/demande-recue", context);

      sendHtml(
              to,
              "Votre demande de devis a bien été reçue",
              html
      );

    } catch (Exception e) {
      log.error("❌ Erreur confirmation demande", e);
    }
  }

  /* =====================================================
     🔹 RÉPONSE ADMIN DEVIS
     ===================================================== */
  @Async
  public void sendDevisResponse(
          String to,
          String clientName,
          String message,
          String pieceJointePath
  ) {
    try {

      boolean hasAttachment =
              pieceJointePath != null && !pieceJointePath.isBlank();

      Context context = new Context();
      context.setVariable("name", clientName);
      context.setVariable("message", message);
      context.setVariable("year", Year.now().getValue());
      context.setVariable("hasAttachment", hasAttachment);

      String html =
              templateEngine.process("mail/devis-reponse", context);

      MimeMessage mimeMessage = mailSender.createMimeMessage();

      MimeMessageHelper helper =
              new MimeMessageHelper(
                      mimeMessage,
                      hasAttachment,
                      StandardCharsets.UTF_8.name()
              );

      helper.setFrom(mailProperties.getFrom());
      helper.setTo(to);
      helper.setSubject("Réponse à votre demande de devis");
      helper.setText(html, true);

      /* 📎 PJ */
      if (hasAttachment) {

        Path path = Path.of(pieceJointePath);

        if (Files.exists(path)) {
          helper.addAttachment(
                  path.getFileName().toString(),
                  new FileSystemResource(path.toFile())
          );
        } else {
          log.warn("⚠️ PJ introuvable : {}", pieceJointePath);
        }
      }

      mailSender.send(mimeMessage);

      log.info("📨 Réponse devis envoyée à {}", to);

    } catch (Exception e) {
      log.error("❌ Erreur envoi réponse devis", e);
    }
  }

  /* =====================================================
     🔹 BROCHURE FORMATION
     ===================================================== */
  @Async
  public void sendFormationBrochure(
          String to,
          String name,
          String formation,
          String pdfUrl
  ) {
    try {

      Context context = new Context();
      context.setVariable("name", name);
      context.setVariable("formation", formation);
      context.setVariable("pdfUrl", pdfUrl);
      context.setVariable("year", Year.now().getValue());

      String html = templateEngine.process("mail/brochure", context);

      sendHtml(
              to,
              "Votre brochure de formation",
              html
      );

      log.info("📘 Brochure envoyée à {}", to);

    } catch (Exception e) {
      log.error("❌ Erreur envoi brochure", e);
    }
  }


  /* =====================================================
   🔹 PRÉINSCRIPTION — ACCUSÉ DE RÉCEPTION
   ===================================================== */
  @Async
  public void sendPreinscriptionRecue(
          String to,
          String civilite,
          String nom,
          String formation,
          String niveau,
          String anneeUniv
  ) {
    try {
      Context context = new Context();
      context.setVariable("civilite",  civilite);
      context.setVariable("nom",       nom.toUpperCase());
      context.setVariable("formation", formation);
      context.setVariable("niveau",    niveau);
      context.setVariable("anneeUniv", anneeUniv);
      context.setVariable("year",      Year.now().getValue());

      String html = templateEngine.process(
              "mail/preinscription-recue", context
      );

      sendHtml(
              to,
              "Demande de préinscription reçue – " + anneeUniv,
              html
      );

      log.info("📩 Accusé préinscription envoyé à {}", to);

    } catch (Exception e) {
      log.error("❌ Erreur accusé préinscription", e);
    }
  }

  /* =====================================================
     🔹 PRÉINSCRIPTION — ATTESTATION VALIDÉE + PDF
     ===================================================== */
  @Async
  public void sendPreinscriptionValidee(
          String to,
          String civilite,
          String nom,
          String formation,
          String niveau,
          String anneeUniv,
          String pdfPath
  ) {
    try {
      Context context = new Context();
      context.setVariable("civilite",  civilite);
      context.setVariable("nom",       nom.toUpperCase());
      context.setVariable("formation", formation);
      context.setVariable("niveau",    niveau);
      context.setVariable("anneeUniv", anneeUniv);
      context.setVariable("year",      Year.now().getValue());

      String html = templateEngine.process(
              "mail/preinscription-validee", context
      );

      sendHtmlWithAttachmentFromPath(
              to,
              "Votre attestation de préinscription – " + anneeUniv,
              html,
              pdfPath
      );

      log.info("📨 Attestation préinscription envoyée à {}", to);

    } catch (Exception e) {
      log.error("❌ Erreur envoi attestation préinscription", e);
    }
  }
}