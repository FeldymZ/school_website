package com.school.api.agenda.controller;

import com.school.api.agenda.dto.AgendaCalendarResponse;
import com.school.api.agenda.dto.AgendaEventResponse;
import com.school.api.agenda.dto.AgendaEventUpdateRequest;
import com.school.api.agenda.service.AgendaEventService;
import com.school.api.auth.audit.AuditLog;
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

  @AuditLog(action = "CONSULTATION_AGENDA")
  @GetMapping
  public List<AgendaEventResponse> all() {
    return service.getAll();
  }

  @AuditLog(action = "CREATION_EVENEMENT", target = "#title", failureAction = "CREATION_EVENEMENT_ECHEC")
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
    return service.create(title, description, eventDate, endDate, startTime, endTime, location, enabled);
  }

  @AuditLog(action = "MODIFICATION_EVENEMENT", target = "#id.toString()", failureAction = "MODIFICATION_EVENEMENT_ECHEC")
  @PutMapping("/{id}")
  public AgendaEventResponse update(
          @PathVariable Long id,
          @RequestBody AgendaEventUpdateRequest request
  ) {
    return service.update(id, request);
  }

  @AuditLog(action = "SUPPRESSION_EVENEMENT", target = "#id.toString()", failureAction = "SUPPRESSION_EVENEMENT_ECHEC")
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @AuditLog(action = "CONSULTATION_CALENDRIER_AGENDA")
  @GetMapping("/calendar")
  public AgendaCalendarResponse calendar(
          @RequestParam int year,
          @RequestParam int month
  ) {
    return service.getCalendarByMonthAdmin(year, month);
  }
}