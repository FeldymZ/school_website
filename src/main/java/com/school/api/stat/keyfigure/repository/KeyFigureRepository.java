package com.school.api.stat.keyfigure.repository;

import com.school.api.stat.keyfigure.entity.KeyFigure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeyFigureRepository
  extends JpaRepository<KeyFigure, Long> {

  // ADMIN : tout, trié
  List<KeyFigure> findAllByOrderByDisplayOrderAsc();

  // PUBLIC : seulement les actifs, triés
  List<KeyFigure> findByEnabledTrueOrderByDisplayOrderAsc();
}
