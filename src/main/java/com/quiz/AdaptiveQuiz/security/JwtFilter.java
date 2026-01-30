package com.quiz.AdaptiveQuiz.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    // We need to check DB for session ID
    private final com.quiz.AdaptiveQuiz.repository.UserRepository userRepo;

    public JwtFilter(JwtUtil jwtUtil, com.quiz.AdaptiveQuiz.repository.UserRepository userRepo) {
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                if (jwtUtil.isTokenValid(token)) {

                    String email = jwtUtil.extractEmail(token);
                    String sessionId = jwtUtil.extractSessionId(token);

                    // Fetch user and validate session
                    com.quiz.AdaptiveQuiz.entity.User user = userRepo.findByEmail(email).orElse(null);

                    if (user != null && sessionId != null && sessionId.equals(user.getSessionId())) {
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                Collections.emptyList());

                        auth.setDetails(
                                new WebAuthenticationDetailsSource()
                                        .buildDetails(request));

                        SecurityContextHolder
                                .getContext()
                                .setAuthentication(auth);
                    } else {
                        // Session invalid or user not found
                        System.out.println("Session Invalid: Token=" + sessionId + " DB="
                                + (user != null ? user.getSessionId() : "null"));
                    }
                }
            } catch (Exception e) {
                // ❌ DO NOT block
                // ❌ DO NOT set response status
                System.out.println("JWT ERROR: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
