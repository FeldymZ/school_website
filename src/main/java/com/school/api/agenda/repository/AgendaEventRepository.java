package com.school.api.agenda.repository;

import com.school.api.agenda.entity.AgendaEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AgendaEventRepository extends JpaRepository<AgendaEvent, Long> {

  /* 🌍 À venir */
  List<AgendaEvent> findByEnabledTrueAndEventDateGreaterThanEqualOrderByEventDateAsc(
    LocalDate today
  );

  /* 🌍 Passés */
  List<AgendaEvent> findByEnabledTrueAndEventDateLessThanOrderByEventDateDesc(
    LocalDate today
  );

  /* 🌍 Mois */
  List<AgendaEvent> findByEnabledTrueAndEventDateBetweenOrderByEventDateAsc(
    LocalDate start,
    LocalDate end
  );
}
