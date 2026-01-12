package com.school.api.common.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.Year;

@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender mailSender;

  /* ============================
     EMAIL HTML SANS PJ
     ============================ */

  public void sendHtml(
    String to,
    String subject,
    String htmlContent
  ) {

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper =
        new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());

      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      mailSender.send(message);

    } catch (Exception e) {
      throw new RuntimeException("Erreur envoi email HTML", e);
    }
  }

  /* ============================
     EMAIL HTML AVEC PJ
     ============================ */

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

      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      helper.addAttachment(
        attachment.getOriginalFilename(),
        new ByteArrayResource(attachment.getBytes())
      );

      mailSender.send(message);

    } catch (Exception e) {
      throw new RuntimeException("Erreur email HTML + PJ", e);
    }
  }
}
