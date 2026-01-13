package com.school.api.common.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class MailTemplateService {

  private final TemplateEngine templateEngine;

  public String buildContactReply(String name, String replyMessage) {

    Context context = new Context();
    context.setVariable("name", name);
    context.setVariable("replyMessage", replyMessage);
    context.setVariable("year", Year.now().getValue());

    return templateEngine.process("mail/contact-reply", context);
  }
}
