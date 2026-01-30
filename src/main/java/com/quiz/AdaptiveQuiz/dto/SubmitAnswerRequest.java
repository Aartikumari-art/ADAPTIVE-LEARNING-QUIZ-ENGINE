package com.quiz.AdaptiveQuiz.dto;

public class SubmitAnswerRequest {

    private Long attemptId;
    private String questionText;
    private String selectedAnswer;
    private String correctAnswer;

    public Long getAttemptId() {
        return attemptId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
