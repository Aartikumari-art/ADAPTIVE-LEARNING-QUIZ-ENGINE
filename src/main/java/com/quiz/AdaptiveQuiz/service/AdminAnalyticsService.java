package com.quiz.AdaptiveQuiz.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.quiz.AdaptiveQuiz.repository.QuizAttemptRepository;
import com.quiz.AdaptiveQuiz.repository.UserRepository;
import com.quiz.AdaptiveQuiz.entity.User;
import java.nio.charset.StandardCharsets;

@Service
public class AdminAnalyticsService {

    private final UserRepository userRepo;
    private final QuizAttemptRepository repo;
    private final com.quiz.AdaptiveQuiz.repository.UserResponseRepository responseRepo;

    public AdminAnalyticsService(UserRepository userRepo,
            QuizAttemptRepository repo,
            com.quiz.AdaptiveQuiz.repository.UserResponseRepository responseRepo) {
        this.userRepo = userRepo;
        this.repo = repo;
        this.responseRepo = responseRepo;
    }

    public Double getSystemStats() {
        return repo.getSystemAverageAccuracy();
    }

    public List<Object[]> getSubjectAnalytics() {
        return repo.subjectAnalytics();
    }

    public List<Object[]> getUserAnalytics() {
        return repo.userAnalytics();
    }

    public List<Object[]> getDifficultyDistribution() {
        return responseRepo.findDifficultyDistribution();
    }

    public byte[] generateUserReportCsv() {
        List<Object[]> users = userRepo.findAllWithStats();
        StringBuilder csv = new StringBuilder();
        csv.append("User ID,Name,Email,Role,Avg Accuracy,Total Quizzes\n");

        for (Object[] row : users) {
            User u = (User) row[0];
            Double accuracy = (Double) row[1];
            Long count = (Long) row[2];

            csv.append(u.getId()).append(",")
                    .append(escapeCsv(u.getName())).append(",")
                    .append(escapeCsv(u.getEmail())).append(",")
                    .append(u.getRole()).append(",")
                    .append(String.format("%.2f", accuracy)).append("%").append(",")
                    .append(count).append("\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String data) {
        if (data == null)
            return "";
        return "\"" + data.replace("\"", "\"\"") + "\"";
    }
}
