package com.quiz.AdaptiveQuiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.quiz.AdaptiveQuiz.entity.Subject;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByName(String name);

    boolean existsByName(String name);
}
