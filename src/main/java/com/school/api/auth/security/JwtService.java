package com.school.api.auth.security;

import com.school.api.auth.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

  private static final String SECRET =
    "SUPER_SECRET_KEY_CHANGE_ME_SUPER_SECRET_KEY_CHANGE_ME";

  private static final long EXPIRATION = 1000 * 60 * 60 * 24;

  private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

  public String generateToken(User user) {
    return Jwts.builder()
      .setSubject(user.getEmail())
      .claim("role", user.getRole())
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
      .signWith(key)
      .compact();
  }

  public SecretKey getKey() {
    return key;
  }
}
