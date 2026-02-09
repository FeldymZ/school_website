package com.school.api.BannerMessage.repository;

import com.school.api.BannerMessage.entity.BannerMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BannerMessageRepository
  extends JpaRepository<BannerMessage, Long> {

  Optional<BannerMessage> findByActiveTrue();
}
