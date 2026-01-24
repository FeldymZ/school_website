package com.school.api.contact.service;

import com.school.api.common.mail.MailService;
import com.school.api.common.mail.MailTemplateService;
import com.school.api.contact.dto.ContactCreateRequest;
import com.school.api.contact.dto.ContactResponse;
import com.school.api.contact.entity.ContactMessage;
import com.school.api.contact.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

  private final ContactMessageRepository repository;
  private final MailService mailService;
  private final MailTemplateService mailTemplateService;

  /* ============================
     🌍 PUBLIC
     ============================ */

  /**
   * Envoi d’un message par le public (sans pièce jointe)
   */
  public ContactResponse send(ContactCreateRequest request) {

    if (request.senderName() == null || request.senderName().isBlank()) {
      throw new IllegalArgumentException("Le nom est obligatoire");
    }

    if (request.senderEmail() == null || request.senderEmail().isBlank()) {
      throw new IllegalArgumentException("L’email est obligatoire");
    }

    if (request.message() == null || request.message().isBlank()) {
      throw new IllegalArgumentException("Le message est obligatoire");
    }

    ContactMessage message = ContactMessage.builder()
      .senderName(request.senderName())
      .senderEmail(request.senderEmail())
      .message(request.message())
      .sentAt(LocalDateTime.now())
      .replied(false)
      .build();

    return toResponse(repository.save(message));
  }

  /* ============================
     🔐 ADMIN
     ============================ */

  /**
   * Historique complet des messages
   */
  public List<ContactResponse> getAll() {
    return repository.findAllByOrderBySentAtDesc()
      .stream()
      .map(this::toResponse)
      .toList();
  }

  /**
   * Messages non répondus
   */
  public List<ContactResponse> getUnreplied() {
    return repository.findByRepliedFalseOrderBySentAtDesc()
      .stream()
      .map(this::toResponse)
      .toList();
  }

  /**
   * Répondre à un message (email HTML personnalisé + PJ optionnelle)
   */
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

    // 🧩 Génération HTML personnalisé
    String htmlContent = mailTemplateService.buildContactReply(
      message.getSenderName(),
      replyMessage
    );

    // 📧 Envoi de l’email
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

    // 📜 Historique
    message.setReplied(true);
    message.setReplyMessage(replyMessage);
    message.setRepliedAt(LocalDateTime.now());

    repository.save(message);
  }

  /* ============================
     🧩 UTILS
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
