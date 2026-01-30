package com.quiz.AdaptiveQuiz.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.quiz.AdaptiveQuiz.dto.ReviewDTO;
import com.quiz.AdaptiveQuiz.entity.QuizAttempt;
import com.quiz.AdaptiveQuiz.entity.UserResponse;
import com.quiz.AdaptiveQuiz.repository.QuizAttemptRepository;
import com.quiz.AdaptiveQuiz.repository.UserResponseRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ReviewService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final UserResponseRepository userResponseRepository;

    public ReviewService(
            QuizAttemptRepository quizAttemptRepository,
            UserResponseRepository userResponseRepository
    ) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.userResponseRepository = userResponseRepository;
    }

    public List<ReviewDTO> getReviewByAttemptId(Long attemptId) {

        QuizAttempt attempt = quizAttemptRepository
                .findById(attemptId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz attempt not found"));

        List<UserResponse> responses =
                userResponseRepository.findByAttempt(attempt);

        return responses.stream()
                .map(r -> new ReviewDTO(
                        r.getQuestionText(),        // ✅ direct field
                        r.getSelectedAnswer(),      // ✅ direct field
                        r.getCorrectAnswer(),       // ✅ direct field
                        r.isCorrect(),              // ✅ direct field
                        r.getDifficulty().name()    // ✅ enum
                ))
                .collect(Collectors.toList());
    }
}
