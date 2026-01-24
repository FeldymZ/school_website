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

        // 🌍 ORIGINS AUTORISÉS
        config.setAllowedOrigins(List.of(
            "http://localhost:5173",
            "https://test.esiitech-gabon.com",
            "https://esiitech-gabon.com"
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

        // 🔑 JWT / AUTH HEADER
        config.setAllowCredentials(true);

        // (optionnel mais propre)
        config.setExposedHeaders(List.of(
            "Authorization",
            "Content-Type"
        ));

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        // ⚠️ IMPORTANT
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
