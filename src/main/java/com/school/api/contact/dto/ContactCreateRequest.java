package com.school.api.contact.dto;

public record ContactCreateRequest(
  String senderName,
  String senderEmail,
  String message,
  String website   // 👈 honeypot
) {}
