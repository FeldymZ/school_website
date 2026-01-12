package com.school.api.common.mail;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.time.Year;

@Service
public class MailTemplateService {

  public String buildContactReply(String name, String replyMessage) {

    try {
      String template = Files.readString(
        new ClassPathResource("templates/mail/contact-reply.html")
          .getFile().toPath()
      );

      return template
        .replace("{{name}}", name)
        .replace("{{replyMessage}}", replyMessage.replace("\n", "<br>"))
        .replace("{{year}}", String.valueOf(Year.now().getValue()));

    } catch (Exception e) {
      throw new RuntimeException("Erreur template email", e);
    }
  }
}
