package com.school.api.config;

import com.school.api.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(
          HttpSecurity http
  ) throws Exception {

    http

            /* ================= CSRF ================= */

            .csrf(csrf -> csrf.disable())

            /* ================= CORS ================= */

            .cors(Customizer.withDefaults())

            /* ================= SESSION ================= */

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            /* ================= AUTHORIZATION ================= */

            .authorizeHttpRequests(auth -> auth

                    /* ================= AUTH ================= */

                    .requestMatchers(
                            "/api/auth/login",
                            "/api/auth/refresh",
                            "/api/auth/logout"
                    ).permitAll()

                    /* ================= SWAGGER ================= */

                    .requestMatchers(
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/v3/api-docs/**"
                    ).permitAll()

                    /* ================= PUBLIC ================= */

                    .requestMatchers(
                            "/api/public/**",
                            "/files/**",
                            "/assets/**"
                    ).permitAll()

                    /* ================= PDF PREINSCRIPTIONS ================= */

                    .requestMatchers(
                            "/api/admin/preinscriptions/**"
                    ).hasAnyAuthority(
                            "ROLE_SUPERADMIN",
                            "ROLE_ADMIN"
                    )

                    /* ================= PREFLIGHT ================= */

                    .requestMatchers(
                            HttpMethod.OPTIONS,
                            "/**"
                    ).permitAll()

                    /* ================= RESTE ================= */

                    .anyRequest().authenticated()
            )

            /* ================= JWT FILTER ================= */

            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            )

            /* ================= DISABLE ================= */

            .formLogin(form -> form.disable())

            .httpBasic(basic -> basic.disable());

    return http.build();
  }
}