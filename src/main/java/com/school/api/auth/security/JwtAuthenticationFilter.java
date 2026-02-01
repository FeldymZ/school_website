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

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {

    String path = request.getServletPath();

    // ✅ NE PAS FILTRER LOGIN / REFRESH
    if (
      path.startsWith("/api/auth/login") ||
      path.startsWith("/api/auth/refresh")
    ) {
      filterChain.doFilter(request, response);
      return;
    }

    String header = request.getHeader("Authorization");

    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String token = header.substring(7);

      Claims claims = Jwts.parserBuilder()
        .setSigningKey(jwtService.getKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

      String email = claims.getSubject();
      String role = claims.get("role", String.class);

      var auth = new UsernamePasswordAuthenticationToken(
        email,
        null,
        List.of(new SimpleGrantedAuthority("ROLE_" + role))
      );

      SecurityContextHolder.getContext().setAuthentication(auth);

    } catch (Exception e) {
      // ❌ Token expiré ou invalide → on laisse passer (401 sera géré par Spring)
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }
}
