package com.quiz.AdaptiveQuiz.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.quiz.AdaptiveQuiz.entity.User;
import com.quiz.AdaptiveQuiz.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final EmailService emailService;

    public UserService(
            UserRepository repo,
            PasswordEncoder encoder,
            EmailService emailService) {
        this.repo = repo;
        this.encoder = encoder;
        this.emailService = emailService;
    }

    @Transactional
    public void register(
            String name,
            String email,
            String password) {

        if (!com.quiz.AdaptiveQuiz.util.EmailValidator.isValidEmailDomain(email)) {
            throw new RuntimeException("Invalid email domain. Cannot receive emails.");
        }

        if (repo.existsByEmail(email))
            throw new RuntimeException("Email already exists");

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(encoder.encode(password));
        user.setRole("STUDENT");
        user.setVerified(false); // Default false

        // Generate verification token
        String token = java.util.UUID.randomUUID().toString();
        user.setVerificationToken(token);

        repo.save(user); // Save first

        // Send email
        emailService.sendVerificationEmail(email, token);
    }

    public User login(String email, String password) {

        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("Email not verified. Please check your email.");
        }

        if (!encoder.matches(password, user.getPassword()))
            throw new RuntimeException("Invalid password");

        // Generate new session ID
        String sessionId = java.util.UUID.randomUUID().toString();
        user.setSessionId(sessionId);
        repo.save(user);

        return user;
    }

    public void verifyEmail(String email, String token) {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isVerified())
            return; // Already verified

        if (token.equals(user.getVerificationToken())) {
            user.setVerified(true);
            user.setVerificationToken(null);
            repo.save(user);
        } else {
            throw new RuntimeException("Invalid verification token");
        }
    }

    public User processOAuthPostLogin(String email, String name) {
        User user = repo.findByEmail(email).orElse(null);

        if (user == null) {
            // Register new user from OAuth
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setProvider("GOOGLE");
            user.setRole("STUDENT");
            user.setVerified(true); // OAuth emails are verified
            user.setPassword(encoder.encode("OAUTH_USER")); // Dummy password
            repo.save(user);
        } else {
            // Update existing user
            user.setProvider("GOOGLE");
            user.setVerified(true);
            repo.save(user);
        }

        // Generate Session ID for OAuth login too
        String sessionId = java.util.UUID.randomUUID().toString();
        user.setSessionId(sessionId);
        repo.save(user);

        return user;
    }
}
