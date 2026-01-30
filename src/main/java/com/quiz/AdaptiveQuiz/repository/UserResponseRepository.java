package com.quiz.AdaptiveQuiz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quiz.AdaptiveQuiz.entity.QuizAttempt;
import com.quiz.AdaptiveQuiz.entity.UserResponse;

public interface UserResponseRepository
        extends JpaRepository<UserResponse, Long> {

    List<UserResponse> findByAttempt(QuizAttempt attempt);

    UserResponse findTopByAttemptOrderByIdDesc(QuizAttempt attempt);

    long countByAttempt_User_IdAndCorrectTrue(Long userId);

    long countByAttempt_User_IdAndCorrectFalse(Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT u.difficulty, u.correct, COUNT(u) FROM UserResponse u GROUP BY u.difficulty, u.correct")
    List<Object[]> findDifficultyDistribution();
}
