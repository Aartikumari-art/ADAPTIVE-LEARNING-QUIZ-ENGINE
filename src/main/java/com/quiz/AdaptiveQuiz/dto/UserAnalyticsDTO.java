package com.quiz.AdaptiveQuiz.dto;

public class UserAnalyticsDTO {

    private String username;
    private long totalAttempts;
    private double avgAccuracy;
    private int skillScore;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public long getTotalAttempts() { return totalAttempts; }
    public void setTotalAttempts(long totalAttempts) { this.totalAttempts = totalAttempts; }

    public double getAvgAccuracy() { return avgAccuracy; }
    public void setAvgAccuracy(double avgAccuracy) { this.avgAccuracy = avgAccuracy; }

    public int getSkillScore() { return skillScore; }
    public void setSkillScore(int skillScore) { this.skillScore = skillScore; }
}
