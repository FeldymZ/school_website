package com.school.api.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig
        implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(
          ResourceHandlerRegistry registry
  ) {

        /* ============================
           🖼️ BANNERS
           ============================ */

    registry
            .addResourceHandler("/files/banners/**")
            .addResourceLocations("file:/files/banners/");

        /* ============================
           🎓 FORMATIONS INITIALES
           ============================ */

    registry
            .addResourceHandler("/files/formations/initiale/**")
            .addResourceLocations("file:/files/formations/initiale/");

        /* ============================
           🎓 FORMATIONS CONTINUES
           ============================ */

    registry
            .addResourceHandler("/files/formations/continues/**")
            .addResourceLocations("file:/files/formations/continues/");

        /* ============================
           📰 ACTUALITÉS
           ============================ */

    registry
            .addResourceHandler("/files/actualites/**")
            .addResourceLocations("file:/files/actualites/");

        /* ============================
           🤝 PARTENAIRES
           ============================ */

    registry
            .addResourceHandler("/files/partenaires/**")
            .addResourceLocations("file:/files/partenaires/");

        /* ============================
           💬 COMMENTAIRES
           ============================ */

    registry
            .addResourceHandler("/files/commentaires/**")
            .addResourceLocations("file:/files/commentaires/");

        /* ============================
           ✉️ CONTACT
           ============================ */

    registry
            .addResourceHandler("/files/contact/**")
            .addResourceLocations("file:/files/contact/");

        /* ============================
           🏭 VIE ÉTUDIANTE
           ============================ */

    registry
            .addResourceHandler("/files/vie-etudiante/**")
            .addResourceLocations("file:/files/vie-etudiante/");

        /* ============================
           ✍️ SIGNATURES
           ============================ */

    registry
            .addResourceHandler("/files/signatures/**")
            .addResourceLocations("file:/files/signatures/");

        /* ============================
           📄 PREINSCRIPTIONS PDF
           ============================ */

    registry
            .addResourceHandler("/files/preinscriptions/**")
            .addResourceLocations("file:/files/preinscriptions/");
  }
}