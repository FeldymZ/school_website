package com.school.api.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  /* ================= IGNORE PUBLIC ROUTES ================= */

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {

    String path = request.getRequestURI();

    // 🔍 DEBUG (tu peux supprimer après test)
    System.out.println("JWT FILTER PATH = " + path);

    return path.startsWith("/api/public")
            || path.startsWith("/api/auth")
            || path.startsWith("/swagger-ui")
            || path.startsWith("/v3/api-docs");
  }

  /* ================= FILTER ================= */

  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain filterChain
  ) throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    // 🔓 Aucun token → on laisse passer
    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {

      String token = header.substring(7);

      // 🔥 Sécurité contre token invalide
      if (token == null || token.isBlank()
              || token.equals("null")
              || token.equals("undefined")) {
        filterChain.doFilter(request, response);
        return;
      }

      Claims claims = Jwts.parserBuilder()
              .setSigningKey(jwtService.getKey())
              .build()
              .parseClaimsJws(token)
              .getBody();

      String email = claims.getSubject();
      String role = claims.get("role", String.class);

      if (email == null || role == null) {
        filterChain.doFilter(request, response);
        return;
      }

      String authority = role.startsWith("ROLE_")
              ? role
              : "ROLE_" + role;

      var auth = new UsernamePasswordAuthenticationToken(
              email,
              null,
              List.of(new SimpleGrantedAuthority(authority))
      );

      SecurityContextHolder.getContext().setAuthentication(auth);

    } catch (Exception e) {
      // ❌ Token invalide → on nettoie mais on bloque PAS
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }
}