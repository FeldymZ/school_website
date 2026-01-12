package com.school.api.commentaire.repository;

import com.school.api.commentaire.entity.Commentaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentaireRepository
  extends JpaRepository<Commentaire, Long> {

  List<Commentaire> findByEnabledTrueOrderByDisplayOrderAsc();
  List<Commentaire> findAllByOrderByDisplayOrderAsc();

  boolean existsByDisplayOrder(Integer displayOrder);
  boolean existsByDisplayOrderAndIdNot(Integer displayOrder, Long id);
}
