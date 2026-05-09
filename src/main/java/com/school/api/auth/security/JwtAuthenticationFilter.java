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
  protected boolean shouldNotFilter(
          HttpServletRequest request
  ) {

    String path = request.getRequestURI();

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

    String header =
            request.getHeader("Authorization");

    /* ================= NO TOKEN ================= */

    if (
            header == null ||
                    !header.startsWith("Bearer ")
    ) {

      System.out.println("❌ Aucun token JWT");

      filterChain.doFilter(request, response);

      return;
    }

    try {

      String token = header.substring(7);

      /* ================= INVALID TOKEN ================= */

      if (
              token == null ||
                      token.isBlank() ||
                      token.equals("null") ||
                      token.equals("undefined")
      ) {

        System.out.println("❌ Token vide ou invalide");

        filterChain.doFilter(request, response);

        return;
      }

      /* ================= PARSE JWT ================= */

      Claims claims = Jwts.parserBuilder()

              .setSigningKey(
                      jwtService.getKey()
              )

              .build()

              .parseClaimsJws(token)

              .getBody();

      String email =
              claims.getSubject();

      String role =
              claims.get("role", String.class);

      /* ================= DEBUG JWT ================= */

      System.out.println("JWT EMAIL = " + email);
      System.out.println("JWT ROLE = " + role);

      if (
              email == null ||
                      role == null
      ) {

        System.out.println("❌ Email ou rôle absent");

        filterChain.doFilter(request, response);

        return;
      }

      /* ================= ROLE SPRING ================= */

      String authority =
              role.startsWith("ROLE_")
                      ? role
                      : "ROLE_" + role;

      System.out.println(
              "SPRING AUTHORITY = " + authority
      );

      /* ================= AUTHENTICATION ================= */

      var auth =
              new UsernamePasswordAuthenticationToken(
                      email,
                      null,
                      List.of(
                              new SimpleGrantedAuthority(
                                      authority
                              )
                      )
              );

      SecurityContextHolder
              .getContext()
              .setAuthentication(auth);

      System.out.println("✅ Utilisateur authentifié");

    } catch (Exception e) {

      System.out.println(
              "❌ JWT invalide : " + e.getMessage()
      );

      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }
}