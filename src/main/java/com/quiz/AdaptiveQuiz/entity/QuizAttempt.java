package com.quiz.AdaptiveQuiz.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "quiz_attempt")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    // ⭐ REQUIRED FOR ADAPTIVE LOGIC
    @Enumerated(EnumType.STRING)
    @Column(name = "current_difficulty", nullable = false)
    private Difficulty currentDifficulty;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private int skippedAnswers;
    private double accuracy;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<UserResponse> userResponses = new java.util.ArrayList<>();

    // ===== GETTERS & SETTERS =====

    public Long getAttemptId() {
        return attemptId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    // 🔥 THIS WAS MISSING
    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(Difficulty currentDifficulty) {
        this.currentDifficulty = currentDifficulty;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public int getSkippedAnswers() {
        return skippedAnswers;
    }

    public void setSkippedAnswers(int skippedAnswers) {
        this.skippedAnswers = skippedAnswers;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

}
