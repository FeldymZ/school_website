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
            "http://localhost:5173",      // Front public local
            "http://localhost:5174",      // Back-office local ✅
            "https://test.esiitech-gabon.com",
            "https://esiitech-gabon.com",
            "https://admin.esiitech-gabon.com"
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

        // 🔑 AUTH / JWT (cookies ou Authorization)
        config.setAllowCredentials(true);

        // 📤 HEADERS EXPOSÉS AU FRONT
        config.setExposedHeaders(List.of(
            "Authorization",
            "Content-Type"
        ));

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        // ⚠️ IMPORTANT : appliquer à toutes les routes
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
