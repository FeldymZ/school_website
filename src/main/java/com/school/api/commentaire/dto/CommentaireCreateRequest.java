package com.school.api.commentaire.dto;

public record CommentaireCreateRequest(
  String authorName,
  String content,
  String displayDate,
  Integer displayOrder,
  Boolean enabled
) {}
