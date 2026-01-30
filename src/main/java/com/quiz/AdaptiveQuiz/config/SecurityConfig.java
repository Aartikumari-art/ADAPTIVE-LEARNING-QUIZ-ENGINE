package com.quiz.AdaptiveQuiz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.quiz.AdaptiveQuiz.security.JwtFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final com.quiz.AdaptiveQuiz.security.OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final com.quiz.AdaptiveQuiz.security.HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository;

    public SecurityConfig(JwtFilter jwtFilter,
            com.quiz.AdaptiveQuiz.security.OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
            com.quiz.AdaptiveQuiz.security.HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository) {
        this.jwtFilter = jwtFilter;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.cookieAuthorizationRequestRepository = cookieAuthorizationRequestRepository;
    }

    // ✅ MAIN SECURITY CONFIG
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CSRF not needed for JWT
                .csrf(csrf -> csrf.disable())

                // Enable CORS (uses your CorsConfig)
                .cors(Customizer.withDefaults())

                // Session Management (Removed STATELESS to allow OAuth2 session)
                // .sessionManagement(session ->
                // session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/quiz/**").authenticated()
                        .anyRequest().permitAll())

                // JWT filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // OAuth2 Login
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(
                                a -> a.authorizationRequestRepository(cookieAuthorizationRequestRepository))
                        .successHandler(oAuth2LoginSuccessHandler));

        return http.build();
    }
}
