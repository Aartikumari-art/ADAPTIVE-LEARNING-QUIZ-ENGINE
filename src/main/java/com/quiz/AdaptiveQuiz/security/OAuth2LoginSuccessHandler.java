package com.quiz.AdaptiveQuiz.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.quiz.AdaptiveQuiz.entity.User;
import com.quiz.AdaptiveQuiz.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @org.springframework.beans.factory.annotation.Value("${app.frontend.url}")
    private String frontendUrl;

    public OAuth2LoginSuccessHandler(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        // Extract details
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        // Process user (register/update)
        User user = userService.processOAuthPostLogin(email, name);

        // Generate JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getSessionId());

        // Redirect to configurable frontend URL with token
        String redirectUrl = frontendUrl + "/auth/callback?token=" + token;

        response.sendRedirect(redirectUrl);
    }
}
