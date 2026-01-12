package com.school.api.commentaire.dto;

public record CommentaireUpdateRequest(
  String authorName,
  String content,
  String displayDate,
  Integer displayOrder,
  Boolean enabled
) {}
