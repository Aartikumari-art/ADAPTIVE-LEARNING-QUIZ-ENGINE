package com.quiz.AdaptiveQuiz.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

        private static final String SECRET_KEY = "12345678901234567890123456789012"; // EXACT 32 chars

        private static final long EXPIRATION = 1000 * 60 * 60 * 24;

        private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        public String generateToken(String email, String sessionId) {
                return Jwts.builder()
                                .setSubject(email)
                                .claim("sessionId", sessionId)
                                .setIssuedAt(new Date())
                                .setExpiration(
                                                new Date(System.currentTimeMillis() + EXPIRATION))
                                .signWith(key)
                                .compact();
        }

        public boolean isTokenValid(String token) {
                try {
                        Jwts.parserBuilder()
                                        .setSigningKey(key)
                                        .build()
                                        .parseClaimsJws(token);
                        return true;
                } catch (Exception e) {
                        System.out.println("JWT INVALID: " + e.getMessage());
                        return false;
                }
        }

        public String extractEmail(String token) {
                return Jwts.parserBuilder()
                                .setSigningKey(key)
                                .build()
                                .parseClaimsJws(token)
                                .getBody()
                                .getSubject();
        }

        public String extractSessionId(String token) {
                return Jwts.parserBuilder()
                                .setSigningKey(key)
                                .build()
                                .parseClaimsJws(token)
                                .getBody()
                                .get("sessionId", String.class);
        }
}
