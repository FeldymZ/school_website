package com.school.api.commentaire.dto;

import java.util.List;

public record CommentaireReorderRequest(
  List<Long> orderedIds
) {}
