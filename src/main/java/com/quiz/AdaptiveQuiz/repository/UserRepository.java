package com.quiz.AdaptiveQuiz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quiz.AdaptiveQuiz.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @org.springframework.data.jpa.repository.Query("SELECT u, COALESCE(AVG(q.accuracy), 0.0), COUNT(q) FROM User u LEFT JOIN QuizAttempt q ON q.user = u GROUP BY u")
    java.util.List<Object[]> findAllWithStats();
}
