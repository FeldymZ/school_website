package com.school.api.banner.repository;

import com.school.api.banner.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {

  boolean existsByDisplayOrder(Integer displayOrder);

  List<Banner> findAllByOrderByDisplayOrderAsc();

  /* ============================
     🌍 PUBLIC
     ============================ */

  @Query("""
    SELECT b FROM Banner b
    WHERE b.enabled = true
      AND (b.startAt IS NULL OR b.startAt <= :now)
      AND (b.endAt IS NULL OR b.endAt >= :now)
    ORDER BY b.displayOrder ASC
  """)
  List<Banner> findPublic(LocalDateTime now);

  /* ============================
     🔀 DISPLAY ORDER MANAGEMENT
     ============================ */

  @Modifying
  @Query("""
    UPDATE Banner b
    SET b.displayOrder = b.displayOrder + 1
    WHERE b.displayOrder >= :from
  """)
  void shiftDownFrom(Integer from);

  @Modifying
  @Query("""
    UPDATE Banner b
    SET b.displayOrder = b.displayOrder - 1
    WHERE b.displayOrder > :from AND b.displayOrder <= :to
  """)
  void shiftUpBetween(Integer from, Integer to);

  @Modifying
  @Query("""
    UPDATE Banner b
    SET b.displayOrder = b.displayOrder - 1
    WHERE b.displayOrder > :deletedOrder
  """)
  void compactAfterDelete(Integer deletedOrder);
}
