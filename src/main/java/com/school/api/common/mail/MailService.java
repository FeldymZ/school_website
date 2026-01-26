package com.school.api.common.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.time.Year;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender mailSender;
  private final SpringTemplateEngine templateEngine;
  private final MailProperties mailProperties;

  /* ============================
     EMAIL HTML SIMPLE
     ============================ */

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
      log.info("Email HTML envoyé à {}", to);

    } catch (Exception e) {
      log.error("Erreur envoi email HTML", e);
    }
  }

  /* ============================
     EMAIL HTML SIMPLE AVEC REPLY-TO
     (POUR CONTACT PUBLIC)
     ============================ */

  @Async
  public void sendHtml(
    String to,
    String subject,
    String htmlContent,
    String replyTo
  ) {

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper =
        new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());

      helper.setFrom(mailProperties.getFrom());
      helper.setTo(to);
      helper.setReplyTo(replyTo);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      mailSender.send(message);
      log.info("Email HTML envoyé à {} (reply-to {})", to, replyTo);

    } catch (Exception e) {
      log.error("Erreur envoi email HTML avec Reply-To", e);
    }
  }

  /* ============================
     EMAIL HTML AVEC PJ (LEGACY)
     ============================ */

  @Async
  public void sendHtmlWithAttachment(
    String to,
    String subject,
    String htmlContent,
    MultipartFile attachment
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
        attachment.getOriginalFilename(),
        new ByteArrayResource(attachment.getBytes())
      );

      mailSender.send(message);
      log.info("Email HTML + PJ (legacy) envoyé à {}", to);

    } catch (Exception e) {
      log.error("Erreur email HTML + PJ (legacy)", e);
    }
  }

  /* ============================
     EMAIL HTML AVEC PJ (NOUVEAU)
     ============================ */

  @Async
  public void sendHtmlWithAttachment(
    String to,
    String subject,
    String htmlContent,
    String attachmentName,
    InputStreamSource attachment
  ) {

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper =
        new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

      helper.setFrom(mailProperties.getFrom());
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      helper.addAttachment(attachmentName, attachment);

      mailSender.send(message);
      log.info("Email HTML + PJ envoyé à {}", to);

    } catch (Exception e) {
      log.error("Erreur email HTML + PJ", e);
    }
  }

  /* ============================
     EMAIL AVEC TEMPLATE THYMELEAF
     ============================ */

  @Async
  public void sendTemplateMail(
    String to,
    String subject,
    String template,
    Context context
  ) {

    try {
      String html = templateEngine.process(template, context);

      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper =
        new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());

      helper.setFrom(mailProperties.getFrom());
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(html, true);

      mailSender.send(message);
      log.info("Email template envoyé à {}", to);

    } catch (Exception e) {
      log.error("Erreur email template", e);
    }
  }

  /* ============================
     BROCHURE FORMATION
     ============================ */

  @Async
  public void sendFormationBrochure(
    String to,
    String studentName,
    String formationName,
    String pdfUrl
  ) {

    Context context = new Context();
    context.setVariable("name", studentName);
    context.setVariable("formationName", formationName);
    context.setVariable("pdfUrl", pdfUrl);
    context.setVariable("year", Year.now().getValue());

    sendTemplateMail(
      to,
      "Maquette de la formation " + formationName,
      "mail/formation-brochure",
      context
    );
  }
}
