package com.school.api.contact.controller;

import com.school.api.contact.dto.ContactResponse;
import com.school.api.contact.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/contact")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class ContactAdminController {

  private final ContactService service;

  /* ============================
     📜 HISTORIQUE
     ============================ */

  /**
   * Tous les messages
   */
  @GetMapping("/messages")
  public List<ContactResponse> all() {
    return service.getAll();
  }

  /**
   * Messages non répondus
   */
  @GetMapping("/messages/unreplied")
  public List<ContactResponse> unreplied() {
    return service.getUnreplied();
  }

  /* ============================
     ✉️ RÉPONDRE
     ============================ */

  @PutMapping(
    value = "/messages/{id}/reply",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public void reply(
    @PathVariable Long id,
    @RequestParam String replyMessage,
    @RequestParam(required = false) MultipartFile attachment
  ) {
    service.reply(id, replyMessage, attachment);
  }
}
