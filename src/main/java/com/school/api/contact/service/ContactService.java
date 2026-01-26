package com.school.api.contact.service;

import com.school.api.common.mail.MailService;
import com.school.api.common.mail.MailTemplateService;
import com.school.api.common.notification.SlackNotificationService;
import com.school.api.contact.dto.ContactCreateRequest;
import com.school.api.contact.dto.ContactResponse;
import com.school.api.contact.entity.ContactMessage;
import com.school.api.contact.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

  private final ContactMessageRepository repository;
  private final MailService mailService;
  private final MailTemplateService mailTemplateService;
  private final SlackNotificationService slackNotificationService;

  /* ============================
     🌍 PUBLIC
     ============================ */

  public ContactResponse send(ContactCreateRequest request, String ip) {

    if (request.senderName() == null || request.senderName().isBlank()) {
      throw new IllegalArgumentException("Le nom est obligatoire");
    }

    if (request.senderEmail() == null || request.senderEmail().isBlank()) {
      throw new IllegalArgumentException("L’email est obligatoire");
    }

    if (request.message() == null || request.message().isBlank()) {
      throw new IllegalArgumentException("Le message est obligatoire");
    }

    // 🛡️ Anti-spam (1 message / 2 minutes / email)
    if (repository.existsBySenderEmailAndSentAtAfter(
      request.senderEmail(),
      LocalDateTime.now().minusMinutes(2)
    )) {
      throw new IllegalStateException(
        "Veuillez patienter avant d’envoyer un nouveau message."
      );
    }

    ContactMessage message = ContactMessage.builder()
      .senderName(request.senderName())
      .senderEmail(request.senderEmail())
      .message(request.message())
      .sentAt(LocalDateTime.now())
      .replied(false)
      .senderIp(ip)
      .build();

    ContactMessage saved = repository.save(message);

    /* ============================
       📧 MAIL ADMIN (réception directe)
       ============================ */

    String adminHtml = """
      <h3>Nouveau message depuis le site ESIITech</h3>
      <p><strong>Nom :</strong> %s</p>
      <p><strong>Email :</strong> %s</p>
      <hr/>
      <p>%s</p>
    """.formatted(
      saved.getSenderName(),
      saved.getSenderEmail(),
      saved.getMessage()
    );

    mailService.sendHtml(
      "noreply@esiitech-gabon.com",
      "[CONTACT SITE] Nouveau message",
      adminHtml,
      saved.getSenderEmail() // Reply-To
    );

    /* ============================
       📧 CONFIRMATION UTILISATEUR
       ============================ */

    Context ctx = new Context();
    ctx.setVariable("name", saved.getSenderName());
    ctx.setVariable("year", LocalDateTime.now().getYear());

    mailService.sendTemplateMail(
      saved.getSenderEmail(),
      "Confirmation de réception de votre message",
      "mail/contact-confirmation",
      ctx
    );

    /* ============================
       🔔 NOTIFICATION SLACK ADMIN
       ============================ */

    slackNotificationService.notifyNewContact(
      saved.getSenderName(),
      saved.getSenderEmail()
    );

    return toResponse(saved);
  }

  /* ============================
     🔐 ADMIN
     ============================ */

  public List<ContactResponse> getAll() {
    return repository.findAllByOrderBySentAtDesc()
      .stream()
      .map(this::toResponse)
      .toList();
  }

  public List<ContactResponse> getUnreplied() {
    return repository.findByRepliedFalseOrderBySentAtDesc()
      .stream()
      .map(this::toResponse)
      .toList();
  }

  public void reply(
    Long messageId,
    String replyMessage,
    MultipartFile attachment
  ) {

    if (replyMessage == null || replyMessage.isBlank()) {
      throw new IllegalArgumentException("La réponse est obligatoire");
    }

    ContactMessage message = repository.findById(messageId)
      .orElseThrow(() -> new RuntimeException("Message introuvable"));

    String htmlContent = mailTemplateService.buildContactReply(
      message.getSenderName(),
      replyMessage
    );

    if (attachment != null && !attachment.isEmpty()) {
      mailService.sendHtmlWithAttachment(
        message.getSenderEmail(),
        "Réponse à votre message",
        htmlContent,
        attachment
      );
    } else {
      mailService.sendHtml(
        message.getSenderEmail(),
        "Réponse à votre message",
        htmlContent
      );
    }

    message.setReplied(true);
    message.setReplyMessage(replyMessage);
    message.setRepliedAt(LocalDateTime.now());

    repository.save(message);
  }

  /* ============================
     🧩 MAPPING
     ============================ */

  private ContactResponse toResponse(ContactMessage m) {
    return ContactResponse.builder()
      .id(m.getId())
      .senderName(m.getSenderName())
      .senderEmail(m.getSenderEmail())
      .message(m.getMessage())
      .sentAt(m.getSentAt())
      .replied(m.getReplied())
      .repliedAt(m.getRepliedAt())
      .replyMessage(m.getReplyMessage())
      .build();
  }
}
