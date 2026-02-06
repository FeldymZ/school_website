package com.school.api.activite.service;

import com.school.api.activite.dto.ActiviteRequest;
import com.school.api.activite.dto.ActiviteResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ActiviteService {

  ActiviteResponse create(
    ActiviteRequest request,
    MultipartFile[] photos,
    MultipartFile video
  );

  List<ActiviteResponse> getAll();

  ActiviteResponse getById(Long id);

  void delete(Long id);

  void deleteMedia(Long mediaId);
}
