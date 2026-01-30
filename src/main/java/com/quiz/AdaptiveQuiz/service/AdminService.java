package com.quiz.AdaptiveQuiz.service;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.quiz.AdaptiveQuiz.repository.QuizAttemptRepository;

@Service
public class AdminService {

    private final QuizAttemptRepository attemptRepo;

    public AdminService(QuizAttemptRepository attemptRepo) {
        this.attemptRepo = attemptRepo;
    }

    // =========================
    // SYSTEM AVERAGE ACCURACY
    // =========================
    public Double getSystemAverageAccuracy() {
        return attemptRepo.getSystemAverageAccuracy();
    }

    // =========================
    // SUBJECT-WISE ANALYTICS
    // =========================
    public List<Object[]> getSubjectAnalytics() {
        return attemptRepo.subjectAnalytics();
    }

    // =========================
    // USER-WISE ANALYTICS
    // =========================
    public List<Object[]> getUserAnalytics() {
        return attemptRepo.userAnalytics();
    }

    // =========================
    // WEEKLY LEADERBOARD
    // =========================
    public List<Object[]> getWeeklyLeaderboard() {

        LocalDateTime weekStart = LocalDateTime.now()
                .with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);

        return attemptRepo.findWeeklyLeaderboard(weekStart);
    }
}
