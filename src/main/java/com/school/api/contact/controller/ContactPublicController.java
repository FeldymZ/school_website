package com.school.api.contact.controller;

import com.school.api.contact.dto.ContactCreateRequest;
import com.school.api.contact.dto.ContactResponse;
import com.school.api.contact.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/contact")
@RequiredArgsConstructor
public class ContactPublicController {

  private final ContactService service;

  @PostMapping
  public ContactResponse send(@RequestBody ContactCreateRequest request) {
    return service.send(request);
  }
}
