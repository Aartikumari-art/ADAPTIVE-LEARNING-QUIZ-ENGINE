package com.quiz.AdaptiveQuiz.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.quiz.AdaptiveQuiz.entity.QuizAttempt;
import com.quiz.AdaptiveQuiz.entity.User;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    // ================= DASHBOARD =================
    List<QuizAttempt> findByUser(User user);

    List<QuizAttempt> findBySubject(com.quiz.AdaptiveQuiz.entity.Subject subject);

    // ================= ADMIN ANALYTICS =================

    // System average accuracy
    @Query("SELECT AVG(q.accuracy) FROM QuizAttempt q")
    Double getSystemAverageAccuracy();

    // Subject-wise analytics
    @Query("SELECT q.subject, AVG(q.accuracy), COUNT(q) FROM QuizAttempt q GROUP BY q.subject")
    List<Object[]> subjectAnalytics();

    // User-wise analytics
    @Query("SELECT q.user, AVG(q.accuracy), COUNT(q) FROM QuizAttempt q GROUP BY q.user")
    List<Object[]> userAnalytics();

    // Weekly leaderboard
    @Query("""
                SELECT q.user, AVG(q.accuracy)
                FROM QuizAttempt q
                WHERE q.startTime >= :weekStart
                GROUP BY q.user
                ORDER BY AVG(q.accuracy) DESC
            """)
    List<Object[]> findWeeklyLeaderboard(@Param("weekStart") LocalDateTime weekStart);

    long countByUserId(Long userId);

}
