package com.school.api.contact.service;

import com.school.api.common.mail.MailService;
import com.school.api.common.mail.MailTemplateService;
import com.school.api.common.notification.SlackNotificationService;
import com.school.api.contact.dto.ContactCreateRequest;
import com.school.api.contact.dto.ContactResponse;
import com.school.api.contact.entity.ContactMessage;
import com.school.api.contact.entity.ContactReplyAttachment;
import com.school.api.contact.repository.ContactMessageRepository;
import com.school.api.contact.repository.ContactReplyAttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContactService {

  private final ContactMessageRepository repository;
  private final ContactReplyAttachmentRepository attachmentRepository;
  private final MailService mailService;
  private final MailTemplateService mailTemplateService;
  private final SlackNotificationService slackNotificationService;

  private static final String UPLOAD_DIR = "uploads/contact-replies";

  /* ============================
     🌍 PUBLIC
     ============================ */

  public ContactResponse send(ContactCreateRequest request, String ip) {

    if (request.senderName() == null || request.senderName().isBlank())
      throw new IllegalArgumentException("Le nom est obligatoire");

    if (request.senderEmail() == null || request.senderEmail().isBlank())
      throw new IllegalArgumentException("L’email est obligatoire");

    if (request.message() == null || request.message().isBlank())
      throw new IllegalArgumentException("Le message est obligatoire");

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
      saved.getSenderEmail()
    );

    Context ctx = new Context();
    ctx.setVariable("name", saved.getSenderName());
    ctx.setVariable("year", LocalDateTime.now().getYear());

    mailService.sendTemplateMail(
      saved.getSenderEmail(),
      "Confirmation de réception de votre message",
      "mail/contact-confirmation",
      ctx
    );

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
      .stream().map(this::toResponse).toList();
  }

  public List<ContactResponse> getUnreplied() {
    return repository.findByRepliedFalseOrderBySentAtDesc()
      .stream().map(this::toResponse).toList();
  }

  public ContactResponse getOne(Long id) {
    ContactMessage m = repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Message introuvable"));
    return toResponse(m);
  }

  public Page<ContactResponse> search(String q, int page, int size) {
    PageRequest pr = PageRequest.of(
      page, size, Sort.by("sentAt").descending()
    );

    return repository
      .findBySenderEmailContainingIgnoreCaseOrSenderNameContainingIgnoreCase(
        q, q, pr
      )
      .map(this::toResponse);
  }

  public void reply(Long id, String replyMessage, MultipartFile attachment) {

    if (replyMessage == null || replyMessage.isBlank())
      throw new IllegalArgumentException("La réponse est obligatoire");

    ContactMessage message = repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Message introuvable"));

    String html = mailTemplateService.buildContactReply(
      message.getSenderName(),
      replyMessage
    );

    if (attachment != null && !attachment.isEmpty()) {
      try {
        Files.createDirectories(Paths.get(UPLOAD_DIR));

        String storedName =
          System.currentTimeMillis() + "_" + attachment.getOriginalFilename();

        Path path = Paths.get(UPLOAD_DIR).resolve(storedName);
        Files.copy(attachment.getInputStream(), path);

        ContactReplyAttachment att = ContactReplyAttachment.builder()
          .message(message)
          .fileUrl("/api/admin/contact/attachments/" + storedName)
          .originalFilename(attachment.getOriginalFilename())
          .build();

        attachmentRepository.save(att);

        mailService.sendHtmlWithAttachment(
          message.getSenderEmail(),
          "Réponse à votre message",
          html,
          attachment
        );
      } catch (IOException e) {
        throw new RuntimeException("Erreur stockage fichier", e);
      }
    } else {
      mailService.sendHtml(
        message.getSenderEmail(),
        "Réponse à votre message",
        html
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
    Optional<ContactReplyAttachment> att =
      attachmentRepository.findByMessageId(m.getId());

    return ContactResponse.builder()
      .id(m.getId())
      .senderName(m.getSenderName())
      .senderEmail(m.getSenderEmail())
      .message(m.getMessage())
      .sentAt(m.getSentAt())
      .replied(m.getReplied())
      .repliedAt(m.getRepliedAt())
      .replyMessage(m.getReplyMessage())
      .attachmentUrl(att.map(ContactReplyAttachment::getFileUrl).orElse(null))
      .attachmentName(att.map(ContactReplyAttachment::getOriginalFilename).orElse(null))
      .build();
  }
}
