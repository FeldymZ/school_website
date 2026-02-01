package com.school.api.contact.controller;

import com.school.api.contact.dto.ContactResponse;
import com.school.api.contact.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.*;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/admin/contact")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class ContactAdminController {

  private final ContactService service;

  @GetMapping("/messages")
  public List<ContactResponse> all() {
    return service.getAll();
  }

  @GetMapping("/messages/unreplied")
  public List<ContactResponse> unreplied() {
    return service.getUnreplied();
  }

  @GetMapping("/messages/{id}")
  public ContactResponse getOne(@PathVariable Long id) {
    return service.getOne(id);
  }

  @GetMapping("/messages/page")
  public Page<ContactResponse> paged(
    @RequestParam(defaultValue = "") String q,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size
  ) {
    return service.search(q, page, size);
  }

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

  @GetMapping("/attachments/{filename}")
  public ResponseEntity<Resource> download(@PathVariable String filename) {

    Path path = Paths.get("uploads/contact-replies").resolve(filename);
    Resource resource = new FileSystemResource(path);

    return ResponseEntity.ok()
      .header(
        HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + filename + "\""
      )
      .body(resource);
  }
}
