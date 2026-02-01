package com.school.api.agenda.repository;

import com.school.api.agenda.entity.AgendaEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AgendaEventRepository extends JpaRepository<AgendaEvent, Long> {

  // 🌍 PUBLIC
  List<AgendaEvent> findByEnabledTrueAndEventDateGreaterThanEqualOrderByEventDateAsc(
    LocalDate today
  );

  List<AgendaEvent> findByEnabledTrueAndEventDateLessThanOrderByEventDateDesc(
    LocalDate today
  );

  List<AgendaEvent> findByEnabledTrueAndEventDateBetweenOrderByEventDateAsc(
    LocalDate start,
    LocalDate end
  );

  // 🔐 ADMIN (SANS filtre enabled)
  List<AgendaEvent> findByEventDateBetweenOrderByEventDateAsc(
    LocalDate start,
    LocalDate end
  );
}
