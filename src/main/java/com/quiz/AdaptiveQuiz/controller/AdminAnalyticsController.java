package com.quiz.AdaptiveQuiz.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.AdaptiveQuiz.service.AdminAnalyticsService;

@RestController
@RequestMapping("/api/admin/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminAnalyticsController {

    private final AdminAnalyticsService analyticsService;

    public AdminAnalyticsController(AdminAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/system")
    public Double systemStats() {
        return analyticsService.getSystemStats();
    }

    @GetMapping("/subject")
    public List<Object[]> subjectAnalytics() {
        return analyticsService.getSubjectAnalytics();
    }

    @GetMapping("/user")
    public List<Object[]> userAnalytics() {
        return analyticsService.getUserAnalytics();
    }

    @GetMapping("/difficulty")
    public List<Object[]> difficultyAnalytics() {
        return analyticsService.getDifficultyDistribution();
    }

    @GetMapping("/export/users")
    public org.springframework.http.ResponseEntity<byte[]> exportUserReport() {
        byte[] csvData = analyticsService.generateUserReportCsv();

        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=user_report.csv")
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "text/csv")
                .body(csvData);
    }
}
