package com.school.api.BannerMessage.controller;

import com.school.api.BannerMessage.entity.BannerMessage;
import com.school.api.BannerMessage.service.BannerMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/banner-message")
public class BannerMessagePublicController {

  private final BannerMessageService service;

  public BannerMessagePublicController(BannerMessageService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<?> getActiveBanner() {
    BannerMessage banner = service.getActive();

    if (banner == null) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(
      Map.of(
        "title", banner.getTitle(),
        "content", banner.getContent()
      )
    );
  }
}
