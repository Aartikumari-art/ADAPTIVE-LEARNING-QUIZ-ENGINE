package com.quiz.AdaptiveQuiz.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.quiz.AdaptiveQuiz.dto.LoginRequest;
import com.quiz.AdaptiveQuiz.dto.RegisterRequest;
import com.quiz.AdaptiveQuiz.entity.User;
import com.quiz.AdaptiveQuiz.security.JwtUtil;
import com.quiz.AdaptiveQuiz.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        private final UserService userService;
        private final JwtUtil jwtUtil;

        public AuthController(UserService userService,
                        JwtUtil jwtUtil) {
                this.userService = userService;
                this.jwtUtil = jwtUtil;
        }

        // =====================
        // REGISTER
        // =====================
        @PostMapping("/register")
        public ResponseEntity<?> register(
                        @RequestBody RegisterRequest request) {

                try {
                        userService.register(
                                        request.getName(),
                                        request.getEmail(),
                                        request.getPassword());

                        return ResponseEntity.ok("REGISTERED SUCCESSFULLY");
                } catch (RuntimeException e) {
                        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
                }
        }

        // =====================
        // LOGIN
        // =====================
        @GetMapping("/verify")
        public ResponseEntity<?> verifyUser(@RequestParam String email, @RequestParam String token) {
                try {
                        userService.verifyEmail(email, token);
                        return ResponseEntity.ok(Map.of("message", "Email verified successfully! You can now login."));
                } catch (RuntimeException e) {
                        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
                }
        }

        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody LoginRequest request) {

                try {
                        User user = userService.login(
                                        request.getEmail(),
                                        request.getPassword());

                        String token = jwtUtil.generateToken(user.getEmail(), user.getSessionId());

                        return ResponseEntity.ok(
                                        Map.of(
                                                        "token", token,
                                                        "role", user.getRole(),
                                                        "userId", user.getId()));
                } catch (RuntimeException e) {
                        return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
                }
        }
}
