package com.school.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration config = new CorsConfiguration();

    /* ================= ORIGINS ================= */
    // ⚠️ Utiliser allowedOriginPatterns avec credentials = true
    config.setAllowedOriginPatterns(List.of(
            "http://localhost:5173",          // Front public local
            "http://localhost:5174",          // Back-office local
            "https://*.esiitech-gabon.com"    // Domaine prod
    ));

    /* ================= METHODS ================= */
    // 🔥 CORRECTION ICI : AJOUT PATCH
    config.setAllowedMethods(List.of(
            "GET",
            "POST",
            "PUT",
            "PATCH",   // ✅ IMPORTANT (ton bug venait d’ici)
            "DELETE",
            "OPTIONS"
    ));

    /* ================= HEADERS ================= */
    config.setAllowedHeaders(List.of("*"));

    /* ================= AUTH ================= */
    // 🔑 Obligatoire pour JWT / cookies
    config.setAllowCredentials(true);

    /* ================= EXPOSED HEADERS ================= */
    config.setExposedHeaders(List.of(
            "Authorization",
            "Content-Type"
    ));

    /* ================= PREFLIGHT CACHE ================= */
    config.setMaxAge(3600L);

    /* ================= APPLY ================= */
    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", config);

    return source;
  }
}