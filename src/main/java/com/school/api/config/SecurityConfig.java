package com.school.api.config;

import com.school.api.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
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
      .cors(Customizer.withDefaults())
      .sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .authorizeHttpRequests(auth -> auth

        // 🔓 AUTH
        .requestMatchers(
          "/api/auth/login",
          "/api/auth/refresh",
          "/api/auth/logout"
        ).permitAll()

        // Swagger
        .requestMatchers(
          "/swagger-ui.html",
          "/swagger-ui/**",
          "/v3/api-docs/**"
        ).permitAll()

        // Public
        .requestMatchers(
          "/api/public/**",
          "/files/**",
          "/assets/**"
        ).permitAll()

              .requestMatchers(
                      "/api/preinscriptions/*/pdf"
              ).hasRole("SUPERADMIN/ADMIN")

        // Preflight
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

        // 🔐 RESTE
        .anyRequest().authenticated()
      )
      .addFilterBefore(
        jwtAuthenticationFilter,
        UsernamePasswordAuthenticationFilter.class
      )
      .formLogin(form -> form.disable())
      .httpBasic(basic -> basic.disable());

    return http.build();
  }
}

