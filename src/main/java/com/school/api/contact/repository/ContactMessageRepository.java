package com.school.api.contact.repository;

import com.school.api.contact.entity.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

  /**
   * Text search (name or email) combined with an optional "replied" filter.
   * Pass replied = null to return messages regardless of their reply status.
   */
  @Query("""
      SELECT m FROM ContactMessage m
      WHERE (:q = '' OR
             LOWER(m.senderEmail) LIKE LOWER(CONCAT('%', :q, '%')) OR
             LOWER(m.senderName)  LIKE LOWER(CONCAT('%', :q, '%')))
        AND (:replied IS NULL OR m.replied = :replied)
      ORDER BY m.sentAt DESC
      """)
  Page<ContactMessage> search(
          @Param("q") String q,
          @Param("replied") Boolean replied,
          Pageable pageable
  );
}