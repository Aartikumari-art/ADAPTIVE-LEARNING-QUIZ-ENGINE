package com.quiz.AdaptiveQuiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.quiz.AdaptiveQuiz.entity.Question;
import com.quiz.AdaptiveQuiz.entity.Subject;
import com.quiz.AdaptiveQuiz.entity.Difficulty;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // For Seeding
    boolean existsBySubjectAndDifficulty(Subject subject, Difficulty difficulty);

    long countBySubjectAndDifficulty(Subject subject, Difficulty difficulty);

    // For Fallback (Native Query for Randomness)
    // Note: In DB, column is subject_id, and enum is stored as string
    @Query(value = "SELECT * FROM questions q WHERE q.subject_id = :#{#subject.id} AND q.difficulty = :#{#difficulty.name()} ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Question findRandomBySubjectAndDifficulty(@Param("subject") Subject subject,
            @Param("difficulty") Difficulty difficulty);

    // Fallback if difficulty match not found
    @Query(value = "SELECT * FROM questions q WHERE q.subject_id = :#{#subject.id} ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Question findRandomBySubject(@Param("subject") Subject subject);

    List<Question> findBySubject(Subject subject);
}
