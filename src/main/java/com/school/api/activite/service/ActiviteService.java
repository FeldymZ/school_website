package com.school.api.activite.service;

import com.school.api.activite.dto.ActiviteRequest;
import com.school.api.activite.dto.ActiviteResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ActiviteService {

    /* =========================
       CRUD ACTIVITÉ
       ========================= */

  ActiviteResponse create(ActiviteRequest request, MultipartFile photo);

  ActiviteResponse update(Long id, ActiviteRequest request, MultipartFile photo);

  void delete(Long id);

  List<ActiviteResponse> getAll();

  ActiviteResponse getById(Long id);

    /* =========================
       IMAGES
       ========================= */

  void deleteImage(Long imageId);
}
