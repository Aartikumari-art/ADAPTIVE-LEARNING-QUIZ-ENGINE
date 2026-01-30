package com.quiz.AdaptiveQuiz.dto;

import com.quiz.AdaptiveQuiz.entity.Subject;

public class QuizResultResponse {

    private Subject subject;
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private int skippedAnswers;
    private double accuracy;

    public QuizResultResponse(
            Subject subject,
            int totalQuestions,
            int correctAnswers,
            int wrongAnswers,
            int skippedAnswers,
            double accuracy) {

        this.subject = subject;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.skippedAnswers = skippedAnswers;
        this.accuracy = accuracy;
    }

    public Subject getSubject() {
        return subject;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public int getSkippedAnswers() {
        return skippedAnswers;
    }

    public double getAccuracy() {
        return accuracy;
    }
}
