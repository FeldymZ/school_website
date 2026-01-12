package com.school.api.config;

import com.school.api.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )

      .authorizeHttpRequests(auth -> auth

        // ===============================
        // SWAGGER / OPENAPI (OBLIGATOIRE)
        // ===============================
        .requestMatchers(
          "/swagger-ui.html",
          "/swagger-ui/**",
          "/v3/api-docs",
          "/v3/api-docs/**"
        ).permitAll()

        // ===============================
        // PUBLIC API
        // ===============================
        .requestMatchers(
          "/api/auth/**",
          "/api/public/**",
          "/files/**"
        ).permitAll()

        // ===============================
        // TOUT LE RESTE PROTÉGÉ
        // ===============================
        .anyRequest().authenticated()
      )

      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
      .formLogin(form -> form.disable())
      .httpBasic(basic -> basic.disable());

    return http.build();
  }
}
