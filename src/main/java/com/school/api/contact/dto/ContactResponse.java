package com.school.api.contact.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ContactResponse(
  Long id,
  String senderName,
  String senderEmail,
  String message,
  LocalDateTime sentAt,
  Boolean replied,
  LocalDateTime repliedAt,
  String replyMessage
) {}
