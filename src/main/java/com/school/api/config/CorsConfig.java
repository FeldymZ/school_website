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

      // 🌍 ORIGINS AUTORISÉS (IMPORTANT: utiliser patterns pour credentials)
      config.setAllowedOriginPatterns(List.of(
              "http://localhost:5173",          // Front public local
              "http://localhost:5174",          // Back-office local
              "https://*.esiitech-gabon.com"    // Tous les sous-domaines
      ));

      // 🔓 MÉTHODES AUTORISÉES
      config.setAllowedMethods(List.of(
              "GET",
              "POST",
              "PUT",
              "DELETE",
              "OPTIONS"
      ));

      // 🔓 HEADERS AUTORISÉS
      config.setAllowedHeaders(List.of("*"));

      // 🔑 AUTORISER LES COOKIES / JWT
      config.setAllowCredentials(true);

      // 📤 HEADERS EXPOSÉS AU FRONT
      config.setExposedHeaders(List.of(
              "Authorization",
              "Content-Type"
      ));

      // ⏱️ Cache du preflight (optimisation)
      config.setMaxAge(3600L);

      UrlBasedCorsConfigurationSource source =
              new UrlBasedCorsConfigurationSource();

      // ⚠️ APPLIQUER À TOUTES LES ROUTES
      source.registerCorsConfiguration("/**", config);

      return source;
    }
  }