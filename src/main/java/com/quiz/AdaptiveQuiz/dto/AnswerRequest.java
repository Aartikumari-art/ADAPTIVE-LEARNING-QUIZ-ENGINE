package com.quiz.AdaptiveQuiz.dto;

import com.quiz.AdaptiveQuiz.entity.Difficulty;

public class AnswerRequest {

    public Long attemptId;
    public String question;
    public String selected;
    public String correct;
    public Difficulty difficulty;
}
