package com.school.api.commentaire.dto;

import lombok.Builder;

@Builder
public record CommentaireResponse(
  Long id,
  String authorName,
  String content,
  String displayDate,
  String authorImageUrl,
  Boolean enabled
) {}
