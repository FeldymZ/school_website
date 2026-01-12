package com.school.api.agenda.service;

import com.school.api.agenda.dto.*;
import com.school.api.agenda.entity.AgendaEvent;
import com.school.api.agenda.repository.AgendaEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendaEventService {

  private final AgendaEventRepository repository;

  /* ============================
     🌍 À VENIR
     ============================ */

  public List<AgendaEventResponse> getUpcomingEvents() {
    return repository
      .findByEnabledTrueAndEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate.now())
      .stream()
      .map(this::toDto)
      .toList();
  }

  /* ============================
     🌍 PASSÉS
     ============================ */

  public List<AgendaEventResponse> getPastEvents() {
    return repository
      .findByEnabledTrueAndEventDateLessThanOrderByEventDateDesc(LocalDate.now())
      .stream()
      .map(this::toDto)
      .toList();
  }

  /* ============================
     🌍 AGENDA MENSUEL
     ============================ */

  public List<AgendaEventResponse> getByMonth(int year, int month) {

    LocalDate start = LocalDate.of(year, month, 1);
    LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

    return repository
      .findByEnabledTrueAndEventDateBetweenOrderByEventDateAsc(start, end)
      .stream()
      .map(this::toDto)
      .toList();
  }

  /* ============================
     🌍 JOURS AVEC ÉVÉNEMENTS
     ============================ */

  public List<AgendaDayResponse> getDaysWithEvents(int year, int month) {

    LocalDate start = LocalDate.of(year, month, 1);
    LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

    return repository
      .findByEnabledTrueAndEventDateBetweenOrderByEventDateAsc(start, end)
      .stream()
      .collect(Collectors.groupingBy(
        e -> e.getEventDate().getDayOfMonth(),
        Collectors.counting()
      ))
      .entrySet()
      .stream()
      .map(e -> AgendaDayResponse.builder()
        .day(e.getKey())
        .count(e.getValue().intValue())
        .build()
      )
      .sorted(Comparator.comparingInt(AgendaDayResponse::day))
      .toList();
  }

  /* ============================
     🔐 ADMIN
     ============================ */

  public List<AgendaEventResponse> getAll() {
    return repository.findAll()
      .stream()
      .sorted(Comparator.comparing(AgendaEvent::getEventDate))
      .map(this::toDto)
      .toList();
  }

  public AgendaEventResponse create(
    String title,
    String description,
    LocalDate eventDate,
    LocalDate endDate,
    java.time.LocalTime startTime,
    java.time.LocalTime endTime,
    String location,
    Boolean enabled
  ) {

    AgendaEvent event = AgendaEvent.builder()
      .title(title)
      .description(description)
      .eventDate(eventDate)
      .endDate(endDate)
      .startTime(startTime)
      .endTime(endTime)
      .location(location)
      .enabled(enabled != null ? enabled : true)
      .build();

    return toDto(repository.save(event));
  }

  public AgendaEventResponse update(Long id, AgendaEventUpdateRequest request) {

    AgendaEvent event = repository.findById(id)
      .orElseThrow(() -> new RuntimeException("Événement introuvable"));

    if (request.title() != null) event.setTitle(request.title());
    if (request.description() != null) event.setDescription(request.description());
    if (request.eventDate() != null) event.setEventDate(request.eventDate());
    if (request.endDate() != null) event.setEndDate(request.endDate());
    if (request.startTime() != null) event.setStartTime(request.startTime());
    if (request.endTime() != null) event.setEndTime(request.endTime());
    if (request.location() != null) event.setLocation(request.location());
    if (request.enabled() != null) event.setEnabled(request.enabled());

    return toDto(repository.save(event));
  }

  public void delete(Long id) {
    repository.deleteById(id);
  }

  /* ============================
     🧩 MAPPER
     ============================ */

  private AgendaEventResponse toDto(AgendaEvent e) {
    return AgendaEventResponse.builder()
      .id(e.getId())
      .title(e.getTitle())
      .description(e.getDescription())
      .eventDate(e.getEventDate())
      .endDate(e.getEndDate())
      .startTime(e.getStartTime())
      .endTime(e.getEndTime())
      .location(e.getLocation())
      .build();
  }

  public AgendaCalendarResponse getCalendarByMonth(int year, int month) {

    LocalDate monthStart = LocalDate.of(year, month, 1);
    LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

    List<AgendaEvent> events = repository
      .findByEnabledTrueAndEventDateBetweenOrderByEventDateAsc(
        monthStart.minusDays(31), // marge multi-jours
        monthEnd
      );

    Map<LocalDate, List<AgendaCalendarEventResponse>> daysMap = new HashMap<>();

    for (AgendaEvent event : events) {

      LocalDate start = event.getEventDate();
      LocalDate end = event.getEndDate() != null
        ? event.getEndDate()
        : event.getEventDate();

      LocalDate current = start;

      while (!current.isAfter(end)) {

        if (!current.isBefore(monthStart) && !current.isAfter(monthEnd)) {

          daysMap.computeIfAbsent(current, d -> new ArrayList<>())
            .add(
              AgendaCalendarEventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startDate(start)
                .endDate(end)
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .location(event.getLocation())
                .multiDay(!start.equals(end))
                .build()
            );
        }
        current = current.plusDays(1);
      }
    }

    List<AgendaCalendarDayResponse> days = daysMap.entrySet()
      .stream()
      .sorted(Map.Entry.comparingByKey())
      .map(e -> AgendaCalendarDayResponse.builder()
        .date(e.getKey())
        .events(e.getValue())
        .build()
      )
      .toList();

    return AgendaCalendarResponse.builder()
      .year(year)
      .month(month)
      .days(days)
      .build();
  }

}
