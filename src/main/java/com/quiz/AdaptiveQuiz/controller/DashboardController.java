package com.quiz.AdaptiveQuiz.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.quiz.AdaptiveQuiz.dto.UserDashboardDTO;
import com.quiz.AdaptiveQuiz.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<UserDashboardDTO> getDashboard(
            @RequestParam String email) {
        return ResponseEntity.ok(
                dashboardService.getDashboard(email));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard() {
        return ResponseEntity.ok(dashboardService.getLeaderboard());
    }
}
