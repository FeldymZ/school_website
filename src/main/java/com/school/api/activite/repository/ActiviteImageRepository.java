package com.school.api.activite.repository;

import com.school.api.activite.entity.ActiviteImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiviteImageRepository extends JpaRepository<ActiviteImage, Long> {
}
