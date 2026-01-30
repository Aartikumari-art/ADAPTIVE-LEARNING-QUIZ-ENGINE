package com.quiz.AdaptiveQuiz.service;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.quiz.AdaptiveQuiz.entity.AIQuestion;
import com.quiz.AdaptiveQuiz.entity.Difficulty;

@Service
public class AIQuestionService {

    public AIQuestion generateQuestion(Difficulty difficulty) {

        AIQuestion q = new AIQuestion();
        q.setDifficulty(difficulty);

        if (difficulty == Difficulty.EASY) {
            q.setQuestion("What is 2 + 2?");
            q.setOptions(Arrays.asList("2", "3", "4", "5"));
            q.setCorrectAnswer("4");
        }
        else if (difficulty == Difficulty.MEDIUM) {
            q.setQuestion("Which keyword is used to inherit a class in Java?");
            q.setOptions(Arrays.asList("this", "super", "extends", "implements"));
            q.setCorrectAnswer("extends");
        }
        else {
            q.setQuestion("Which collection does not allow duplicate elements?");
            q.setOptions(Arrays.asList("List", "Map", "Set", "ArrayList"));
            q.setCorrectAnswer("Set");
        }

        return q;
    }
}
