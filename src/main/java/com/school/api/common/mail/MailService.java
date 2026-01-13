package com.school.api.common.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender mailSender;

  /* ============================
     EMAIL HTML SANS PJ
     ============================ */

  @Async   // ✅ ASYNCHRONE
  public void sendHtml(String to, String subject, String htmlContent) {

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper =
        new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());

      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      mailSender.send(message);

      log.info(" Email HTML envoyé à {}", to);

    } catch (Exception e) {
      // ❌ NE PAS throw
      log.error(" Erreur envoi email HTML vers {}", to, e);
    }
  }

  /* ============================
     EMAIL HTML AVEC PJ
     ============================ */

  @Async   // ✅ ASYNCHRONE
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

      log.info("📧 Email HTML + PJ envoyé à {}", to);

    } catch (Exception e) {
      log.error(" Erreur email HTML + PJ vers {}", to, e);
    }
  }
}
