package com.quiz.AdaptiveQuiz.dto;

public class ReviewDTO {

    private String questionText;
    private String selectedAnswer;
    private String correctAnswer;
    private boolean correct;
    private String difficulty;

    public ReviewDTO(
            String questionText,
            String selectedAnswer,
            String correctAnswer,
            boolean correct,
            String difficulty
    ) {
        this.questionText = questionText;
        this.selectedAnswer = selectedAnswer;
        this.correctAnswer = correctAnswer;
        this.correct = correct;
        this.difficulty = difficulty;
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

    public boolean isCorrect() {
        return correct;
    }

    public String getDifficulty() {
        return difficulty;
    }
}
