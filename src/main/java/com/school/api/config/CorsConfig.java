package com.school.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {

        CorsConfiguration config = new CorsConfiguration();

        // 🔓 ORIGINS AUTORISÉS
        config.setAllowedOrigins(List.of(
            "http://localhost:5173",
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

        // 🔑 AUTHORIZATION (JWT)
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
