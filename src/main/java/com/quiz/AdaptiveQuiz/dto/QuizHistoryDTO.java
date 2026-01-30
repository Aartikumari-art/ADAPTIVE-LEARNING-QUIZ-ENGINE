package com.quiz.AdaptiveQuiz.dto;

import java.time.LocalDateTime;

import com.quiz.AdaptiveQuiz.entity.Subject;

public class QuizHistoryDTO {

    private Subject subject;
    private int correctAnswers;
    private int totalQuestions;
    private double accuracy;
    private LocalDateTime attemptedAt;

    public QuizHistoryDTO() {}

    public QuizHistoryDTO(
            Subject subject,
            int correctAnswers,
            int totalQuestions,
            double accuracy,
            LocalDateTime attemptedAt
    ) {
        this.subject = subject;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.accuracy = accuracy;
        this.attemptedAt = attemptedAt;
    }

    // getters
    public Subject getSubject() { return subject; }
    public int getCorrectAnswers() { return correctAnswers; }
    public int getTotalQuestions() { return totalQuestions; }
    public double getAccuracy() { return accuracy; }
    public LocalDateTime getAttemptedAt() { return attemptedAt; }
}
