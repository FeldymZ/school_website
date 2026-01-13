package com.school.api.banner.controller;

import com.school.api.banner.dto.BannerResponse;
import com.school.api.banner.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/banners")
@RequiredArgsConstructor
public class BannerPublicController {

  private final BannerService service;

  @GetMapping
  public List<BannerResponse> get() {
    return service.getPublicBanners();
  }
}
