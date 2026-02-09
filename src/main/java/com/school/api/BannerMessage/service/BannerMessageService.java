package com.school.api.BannerMessage.service;

import com.school.api.BannerMessage.entity.BannerMessage;
import com.school.api.BannerMessage.repository.BannerMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BannerMessageService {

  private final BannerMessageRepository repository;

  public BannerMessageService(BannerMessageRepository repository) {
    this.repository = repository;
  }

  /* ================= PUBLIC ================= */

  public BannerMessage getActive() {
    return repository.findByActiveTrue().orElse(null);
  }

  public List<BannerMessage> getAll() {
    return repository.findAll();
  }

  /* ================= ADMIN ================= */

  public BannerMessage create(
    String title,
    String content,
    boolean active
  ) {
    if (active) {
      disableAll();
    }

    BannerMessage banner = new BannerMessage();
    banner.setTitle(title);
    banner.setContent(content);
    banner.setActive(active);

    return repository.save(banner);
  }

  /**
   * ✅ UPDATE PARTIEL (aucun champ écrasé)
   */
  public BannerMessage updatePartial(
    Long id,
    String title,
    String content,
    Boolean active
  ) {
    BannerMessage banner = repository.findById(id)
      .orElseThrow(() -> new RuntimeException("BannerMessage introuvable"));

    if (active != null && active) {
      disableAll();
    }

    if (title != null) {
      banner.setTitle(title);
    }

    if (content != null) {
      banner.setContent(content);
    }

    if (active != null) {
      banner.setActive(active);
    }

    return repository.save(banner);
  }

  /**
   * ✅ ENDPOINT DÉDIÉ ACTIF / INACTIF
   */
  public BannerMessage setActive(Long id, boolean active) {
    BannerMessage banner = repository.findById(id)
      .orElseThrow(() -> new RuntimeException("BannerMessage introuvable"));

    if (active) {
      disableAll();
    }

    banner.setActive(active);
    return repository.save(banner);
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new RuntimeException("BannerMessage introuvable");
    }
    repository.deleteById(id);
  }

  /* ================= INTERNAL ================= */

  private void disableAll() {
    repository.findAll().forEach(b -> {
      if (b.isActive()) {
        b.setActive(false);
        repository.save(b);
      }
    });
  }
}
