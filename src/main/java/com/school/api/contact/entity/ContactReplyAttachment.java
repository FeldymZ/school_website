package com.school.api.contact.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contact_reply_attachments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactReplyAttachment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "contact_message_id", nullable = false, unique = true)
  private ContactMessage message;

  @Column(nullable = false)
  private String fileUrl;

  @Column(nullable = false)
  private String originalFilename;
}
