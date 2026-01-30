package com.quiz.AdaptiveQuiz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quiz.AdaptiveQuiz.entity.SkillSnapshot;
import com.quiz.AdaptiveQuiz.entity.Subject;
import com.quiz.AdaptiveQuiz.entity.User;

@Repository
public interface SkillSnapshotRepository extends JpaRepository<SkillSnapshot, Long> {

    List<SkillSnapshot> findByUserAndSubjectOrderByTimestampAsc(User user, Subject subject);

    List<SkillSnapshot> findByUser_Email(String email);

    List<SkillSnapshot> findBySubject(Subject subject);

    List<SkillSnapshot> findByUser(User user);

}
