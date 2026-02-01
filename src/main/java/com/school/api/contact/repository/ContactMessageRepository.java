package com.school.api.contact.repository;

import com.school.api.contact.entity.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ContactMessageRepository
  extends JpaRepository<ContactMessage, Long> {

  List<ContactMessage> findByRepliedFalseOrderBySentAtDesc();

  List<ContactMessage> findAllByOrderBySentAtDesc();

  boolean existsBySenderEmailAndSentAtAfter(
    String senderEmail,
    LocalDateTime time
  );

  Page<ContactMessage>
    findBySenderEmailContainingIgnoreCaseOrSenderNameContainingIgnoreCase(
      String email,
      String name,
      Pageable pageable
    );
}
