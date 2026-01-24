package com.school.api.agenda.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "agenda_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /* 📝 Infos */
  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  private String location;

  /* 📅 Dates */
  @Column(nullable = false)
  private LocalDate eventDate;

  private LocalDate endDate;

  private LocalTime startTime;
  private LocalTime endTime;

  /* 🔧 État */
  @Column(nullable = false)
  private Boolean enabled;
}
