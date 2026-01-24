package com.school.api.contact.repository;

import com.school.api.contact.entity.ContactReplyAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactReplyAttachmentRepository
  extends JpaRepository<ContactReplyAttachment, Long> {

  Optional<ContactReplyAttachment> findByMessageId(Long messageId);
}
