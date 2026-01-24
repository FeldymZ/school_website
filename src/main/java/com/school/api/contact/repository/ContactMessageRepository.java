package com.school.api.contact.repository;

import com.school.api.contact.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ContactMessageRepository
  extends JpaRepository<ContactMessage, Long> {

  // Messages non encore répondus
  List<ContactMessage> findByRepliedFalseOrderBySentAtDesc();

  // Tous les messages (admin)
  List<ContactMessage> findAllByOrderBySentAtDesc();

  boolean existsBySenderEmailAndSentAtAfter(
    String senderEmail,
    LocalDateTime time
  );


}
