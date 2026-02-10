package com.quiz.AdaptiveQuiz.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    private final com.quiz.AdaptiveQuiz.repository.UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private final com.quiz.AdaptiveQuiz.repository.QuizAttemptRepository quizAttemptRepo;
    private final com.quiz.AdaptiveQuiz.repository.SkillSnapshotRepository skillSnapshotRepo;

    public UserController(com.quiz.AdaptiveQuiz.repository.UserRepository userRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
            com.quiz.AdaptiveQuiz.repository.QuizAttemptRepository quizAttemptRepo,
            com.quiz.AdaptiveQuiz.repository.SkillSnapshotRepository skillSnapshotRepo) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.quizAttemptRepo = quizAttemptRepo;
        this.skillSnapshotRepo = skillSnapshotRepo;
    }

    @DeleteMapping("/{id}")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            com.quiz.AdaptiveQuiz.entity.User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // Explicitly delete children
            List<com.quiz.AdaptiveQuiz.entity.QuizAttempt> attempts = quizAttemptRepo.findByUser(user);
            quizAttemptRepo.deleteAll(attempts);

            List<com.quiz.AdaptiveQuiz.entity.SkillSnapshot> snapshots = skillSnapshotRepo.findByUser(user);
            skillSnapshotRepo.deleteAll(snapshots);

            userRepository.delete(user);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting user: " + e.getMessage());
        }
    }

    // ================= GET PROFILE =================
    @GetMapping("/profile")
    public org.springframework.http.ResponseEntity<com.quiz.AdaptiveQuiz.dto.UserProfileDTO> getProfile(
            @RequestParam String email) {
        com.quiz.AdaptiveQuiz.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int completion = calculateCompletion(user);

        return org.springframework.http.ResponseEntity.ok(new com.quiz.AdaptiveQuiz.dto.UserProfileDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getBio(),
                user.getAddress(),
                user.getRole(),
                user.getProvider(),
                completion));
    }

    // ================= UPDATE PROFILE =================
    @PutMapping("/profile")
    public org.springframework.http.ResponseEntity<?> updateProfile(@RequestParam String email,
            @RequestBody com.quiz.AdaptiveQuiz.dto.UserProfileDTO dto) {
        com.quiz.AdaptiveQuiz.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getName() != null)
            user.setName(dto.getName());
        if (dto.getPhone() != null)
            user.setPhone(dto.getPhone());
        if (dto.getBio() != null)
            user.setBio(dto.getBio());
        if (dto.getAddress() != null)
            user.setAddress(dto.getAddress());

        userRepository.save(user);
        return org.springframework.http.ResponseEntity.ok("Profile updated successfully");
    }

    // Helper: Calculate Profile Completion %
    private int calculateCompletion(com.quiz.AdaptiveQuiz.entity.User user) {
        int score = 0;
        int totalFields = 5; // Name, Email, Phone, Bio, Address

        if (user.getName() != null && !user.getName().isEmpty())
            score++;
        if (user.getEmail() != null && !user.getEmail().isEmpty())
            score++;
        if (user.getPhone() != null && !user.getPhone().isEmpty())
            score++;
        if (user.getBio() != null && !user.getBio().isEmpty())
            score++;
        if (user.getAddress() != null && !user.getAddress().isEmpty())
            score++;

        return (score * 100) / totalFields;
    }

    // ================= UPLOAD IMAGE =================
    @PostMapping("/image")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("email") String email,
            @RequestParam("file") MultipartFile file) {
        try {
            com.quiz.AdaptiveQuiz.entity.User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setProfileImage(file.getBytes());
            userRepository.save(user);

            return ResponseEntity.ok("Image uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error uploading image");
        }
    }

    // ================= CHANGE PASSWORD =================
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String email,
            @RequestBody java.util.Map<String, String> passwords) {
        com.quiz.AdaptiveQuiz.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String oldPass = passwords.get("oldPassword");
        String newPass = passwords.get("newPassword");

        // Verify Old Password
        if (!passwordEncoder.matches(oldPass, user.getPassword())) {
            return ResponseEntity.badRequest().body("Incorrect old password");
        }

        // Encode New Password
        user.setPassword(passwordEncoder.encode(newPass));
        userRepository.save(user);

        return ResponseEntity.ok("Password changed successfully");
    }

    // ================= DELETE USER (ADMIN) =================
    // Removed duplicate deleteUser method

    // ================= GET ALL USERS (ADMIN) =================
    @GetMapping("/all")
    public java.util.List<Object[]> getAllUsers() {
        return userRepository.findAllWithStats();
    }
}
