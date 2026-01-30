package com.quiz.AdaptiveQuiz.dto;

public class AdminStatsDTO {

    private long totalUsers;
    private long totalQuizAttempts;
    private double averageAccuracy;

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalQuizAttempts() { return totalQuizAttempts; }
    public void setTotalQuizAttempts(long totalQuizAttempts) { this.totalQuizAttempts = totalQuizAttempts; }

    public double getAverageAccuracy() { return averageAccuracy; }
    public void setAverageAccuracy(double averageAccuracy) { this.averageAccuracy = averageAccuracy; }
}
