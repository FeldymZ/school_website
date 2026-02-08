package com.school.api.auth.security;

import com.school.api.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

  private static final String SECRET =
    "SUPER_SECRET_KEY_CHANGE_ME_SUPER_SECRET_KEY_CHANGE_ME";

  // ⏱ NOUVELLES DURÉES
  private static final long ACCESS_EXPIRATION = 1000L * 60 * 60; // 1 heure
  private static final long REFRESH_EXPIRATION = 1000L * 60 * 60 * 24; // 24 heures MAX

  private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

  /* ================= ACCESS TOKEN ================= */
  public String generateAccessToken(User user) {
    return Jwts.builder()
      .setSubject(user.getEmail())
      .claim("role", user.getRole().name())
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
      .signWith(key)
      .compact();
  }

  /* ================= REFRESH TOKEN ================= */
  public String generateRefreshToken(User user) {
    return Jwts.builder()
      .setSubject(user.getEmail())
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
      .signWith(key)
      .compact();
  }

  /* ================= PARSE ================= */
  public Claims parse(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(key)
      .build()
      .parseClaimsJws(token)
      .getBody();
  }

  public SecretKey getKey() {
    return key;
  }
}
