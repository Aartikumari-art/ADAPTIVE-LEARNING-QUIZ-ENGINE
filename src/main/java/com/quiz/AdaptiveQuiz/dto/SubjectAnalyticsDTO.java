package com.quiz.AdaptiveQuiz.dto;

import com.quiz.AdaptiveQuiz.entity.Subject;

public class SubjectAnalyticsDTO {

    private Subject subject;
    private int totalAttempts;
    private double averageAccuracy;
    private double skillScore;

    public SubjectAnalyticsDTO(Subject subject, int totalAttempts, double averageAccuracy, double skillScore) {
        this.subject = subject;
        this.totalAttempts = totalAttempts;
        this.averageAccuracy = averageAccuracy;
        this.skillScore = skillScore;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(int totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public double getAverageAccuracy() {
        return averageAccuracy;
    }

    public void setAverageAccuracy(double averageAccuracy) {
        this.averageAccuracy = averageAccuracy;
    }

    public double getSkillScore() {
        return skillScore;
    }

    public void setSkillScore(double skillScore) {
        this.skillScore = skillScore;
    }
}
