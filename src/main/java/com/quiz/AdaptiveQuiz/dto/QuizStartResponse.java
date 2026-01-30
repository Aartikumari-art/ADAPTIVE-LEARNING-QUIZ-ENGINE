package com.quiz.AdaptiveQuiz.dto;

import com.quiz.AdaptiveQuiz.entity.AIQuestion;

public class QuizStartResponse {

    private Long attemptId;
    private AIQuestion question;

    public QuizStartResponse(Long attemptId, AIQuestion question) {
        this.attemptId = attemptId;
        this.question = question;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public AIQuestion getQuestion() {
        return question;
    }
}
