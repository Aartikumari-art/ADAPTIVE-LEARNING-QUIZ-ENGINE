package com.quiz.AdaptiveQuiz.dto;

public class UserDashboardDTO {

    private int totalQuizzes;
    private int totalCorrect;
    private int totalWrong;
    private double accuracy;
    private int skillScore;
    private String userName;
    private int totalSubjectsAttempted;
    private java.util.List<SubjectAnalyticsDTO> subjectStats;

    public UserDashboardDTO(
            int totalQuizzes,
            int totalCorrect,
            int totalWrong,
            double accuracy,
            int skillScore,
            String userName,
            int totalSubjectsAttempted,
            java.util.List<SubjectAnalyticsDTO> subjectStats) {
        this.totalQuizzes = totalQuizzes;
        this.totalCorrect = totalCorrect;
        this.totalWrong = totalWrong;
        this.accuracy = accuracy;
        this.skillScore = skillScore;
        this.userName = userName;
        this.totalSubjectsAttempted = totalSubjectsAttempted;
        this.subjectStats = subjectStats;
    }

    public int getTotalQuizzes() {
        return totalQuizzes;
    }

    public int getTotalCorrect() {
        return totalCorrect;
    }

    public int getTotalWrong() {
        return totalWrong;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public int getSkillScore() {
        return skillScore;
    }

    public String getUserName() {
        return userName;
    }

    public int getTotalSubjectsAttempted() {
        return totalSubjectsAttempted;
    }

    public java.util.List<SubjectAnalyticsDTO> getSubjectStats() {
        return subjectStats;
    }
}
