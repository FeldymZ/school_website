package com.school.api.BannerMessage.service;

import com.school.api.BannerMessage.entity.BannerMessage;
import com.school.api.BannerMessage.repository.BannerMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerMessageService {

  private final BannerMessageRepository repository;

  public BannerMessageService(BannerMessageRepository repository) {
    this.repository = repository;
  }

  public BannerMessage getActive() {
    return repository.findByActiveTrue().orElse(null);
  }

  public List<BannerMessage> getAll() {
    return repository.findAll();
  }

  public BannerMessage create(String title, String content, boolean active) {
    if (active) disableAll();

    BannerMessage banner = new BannerMessage();
    banner.setTitle(title);
    banner.setContent(content);
    banner.setActive(active);

    return repository.save(banner);
  }

  public BannerMessage update(Long id, String title, String content, boolean active) {
    BannerMessage banner = repository.findById(id)
      .orElseThrow(() -> new RuntimeException("BannerMessage introuvable"));

    if (active) disableAll();

    banner.setTitle(title);
    banner.setContent(content);
    banner.setActive(active);

    return repository.save(banner);
  }

  public void delete(Long id) {
    repository.deleteById(id);
  }

  private void disableAll() {
    repository.findAll().forEach(b -> {
      if (b.isActive()) {
        b.setActive(false);
        repository.save(b);
      }
    });
  }
}
