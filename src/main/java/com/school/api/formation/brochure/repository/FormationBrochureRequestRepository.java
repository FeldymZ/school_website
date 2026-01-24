package com.school.api.formation.brochure.repository;

import com.school.api.formation.brochure.entity.FormationBrochureRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormationBrochureRequestRepository
  extends JpaRepository<FormationBrochureRequest, Long> {
}
