package com.school.api.agenda.controller;

import com.school.api.agenda.dto.AgendaEventResponse;
import com.school.api.agenda.dto.AgendaEventUpdateRequest;
import com.school.api.agenda.service.AgendaEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/agenda")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
public class AgendaAdminController {

  private final AgendaEventService service;

  @GetMapping
  public List<AgendaEventResponse> all() {
    return service.getAll();
  }

  @PostMapping
  public AgendaEventResponse create(
    @RequestParam String title,
    @RequestParam(required = false) String description,
    @RequestParam LocalDate eventDate,
    @RequestParam(required = false) LocalDate endDate,
    @RequestParam(required = false) LocalTime startTime,
    @RequestParam(required = false) LocalTime endTime,
    @RequestParam(required = false) String location,
    @RequestParam(required = false) Boolean enabled
  ) {
    return service.create(
      title,
      description,
      eventDate,
      endDate,
      startTime,
      endTime,
      location,
      enabled
    );
  }

  @PutMapping("/{id}")
  public AgendaEventResponse update(
    @PathVariable Long id,
    @RequestBody AgendaEventUpdateRequest request
  ) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
