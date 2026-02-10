package com.quiz.AdaptiveQuiz.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.quiz.AdaptiveQuiz.entity.Difficulty;
import com.quiz.AdaptiveQuiz.entity.SkillSnapshot;
import com.quiz.AdaptiveQuiz.entity.User;
import com.quiz.AdaptiveQuiz.repository.SkillSnapshotRepository;

@Service
public class AdaptiveLogicService {

    private final SkillSnapshotRepository skillSnapshotRepository;

    public AdaptiveLogicService(SkillSnapshotRepository skillSnapshotRepository) {
        this.skillSnapshotRepository = skillSnapshotRepository;
    }

    // ==================================
    // NEXT DIFFICULTY BASED ON ANSWER
    // ==================================
    public Difficulty nextDifficulty(
            Difficulty current,
            boolean isCorrect) {

        if (isCorrect) {
            if (current == Difficulty.EASY)
                return Difficulty.MEDIUM;
            if (current == Difficulty.MEDIUM)
                return Difficulty.HARD;
        } else {
            if (current == Difficulty.HARD)
                return Difficulty.MEDIUM;
            if (current == Difficulty.MEDIUM)
                return Difficulty.EASY;
        }
        return current;
    }

    // ==================================
    // SAVE SKILL SNAPSHOT (QUIZ END)
    // ==================================
    public void saveSkillSnapshot(
            User user,
            com.quiz.AdaptiveQuiz.entity.Subject subject,
            int correctAnswers,
            int totalQuestions) {

        double accuracy = (double) correctAnswers / totalQuestions;

        // Fetch previous snapshots for THIS subject
        List<SkillSnapshot> snapshots = skillSnapshotRepository.findByUserAndSubjectOrderByTimestampAsc(user, subject);

        int oldSkill = snapshots.isEmpty()
                ? 50
                : (int) snapshots.get(snapshots.size() - 1).getSkillScore();

        // Adaptive formula
        int newSkill = (int) (oldSkill + (accuracy * 10) - 5);

        // Clamp between 0 and 100
        if (newSkill < 0)
            newSkill = 0;
        if (newSkill > 100)
            newSkill = 100;

        SkillSnapshot snapshot = new SkillSnapshot();
        snapshot.setUser(user);
        snapshot.setSubject(subject);
        snapshot.setSkillScore(newSkill);

        skillSnapshotRepository.save(snapshot);
    }
}
