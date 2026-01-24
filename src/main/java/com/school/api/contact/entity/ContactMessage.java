package com.school.api.contact.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 👤 Expéditeur
  @Column(nullable = false)
  private String senderName;

  @Column(nullable = false)
  private String senderEmail;

  // 📝 Message initial
  @Column(nullable = false, columnDefinition = "TEXT")
  private String message;

  // 🕒 Date d’envoi
  @Column(nullable = false)
  private LocalDateTime sentAt;

  // 👁️ Répondu ?
  @Column(nullable = false)
  private Boolean replied;

  // 🕒 Date de réponse
  private LocalDateTime repliedAt;

  // 💬 Contenu de la réponse
  @Column(columnDefinition = "TEXT")
  private String replyMessage;

  // 🛡️ Anti-spam
  private String senderIp;
}
