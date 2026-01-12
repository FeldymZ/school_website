package com.school.api.actualite.repository;

import com.school.api.actualite.entity.ActualitePublicationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActualitePublicationHistoryRepository
  extends JpaRepository<ActualitePublicationHistory, Long> {

  @Query("""
    SELECT h FROM ActualitePublicationHistory h
    WHERE h.actualite.id = :actualiteId
    ORDER BY
      CASE
        WHEN h.action = 'UNPUBLISHED' THEN 0
        WHEN h.action = 'PUBLISHED' THEN 1
        ELSE 2
      END,
      h.actionDate DESC
  """)
  List<ActualitePublicationHistory>
  findByActualiteIdOrdered(Long actualiteId);
}
