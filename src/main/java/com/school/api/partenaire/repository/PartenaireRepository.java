package com.school.api.partenaire.repository;

import com.school.api.partenaire.entity.Partenaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartenaireRepository extends JpaRepository<Partenaire, Long> {

  List<Partenaire> findByEnabledTrueOrderByDisplayOrderAsc();
  List<Partenaire> findAllByOrderByDisplayOrderAsc();

  boolean existsByDisplayOrder(Integer displayOrder);
  boolean existsByDisplayOrderAndIdNot(Integer displayOrder, Long id);
}
