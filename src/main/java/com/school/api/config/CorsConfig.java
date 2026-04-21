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
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "https://*.esiitech-gabon.com"
        ));

        /* ================= METHODS ================= */
        config.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        /* ================= HEADERS ================= */
        config.setAllowedHeaders(List.of("*"));

        /* ================= AUTH ================= */
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
