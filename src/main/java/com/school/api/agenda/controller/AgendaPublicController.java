package com.school.api.agenda.controller;

import com.school.api.agenda.dto.AgendaCalendarResponse;
import com.school.api.agenda.dto.AgendaDayResponse;
import com.school.api.agenda.dto.AgendaEventResponse;
import com.school.api.agenda.service.AgendaEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/agenda")
@RequiredArgsConstructor
public class AgendaPublicController {

  private final AgendaEventService service;

  @GetMapping("/upcoming")
  public List<AgendaEventResponse> upcoming() {
    return service.getUpcomingEvents();
  }

  @GetMapping("/past")
  public List<AgendaEventResponse> past() {
    return service.getPastEvents();
  }

  @GetMapping("/month")
  public List<AgendaEventResponse> byMonth(
    @RequestParam int year,
    @RequestParam int month
  ) {
    return service.getByMonth(year, month);
  }

  @GetMapping("/month/days")
  public List<AgendaDayResponse> days(
    @RequestParam int year,
    @RequestParam int month
  ) {
    return service.getDaysWithEvents(year, month);
  }

  @GetMapping("/calendar")
  public AgendaCalendarResponse calendar(
    @RequestParam int year,
    @RequestParam int month
  ) {
    return service.getCalendarByMonth(year, month);
  }

}
