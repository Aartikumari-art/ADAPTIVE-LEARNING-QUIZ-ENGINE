package com.quiz.AdaptiveQuiz.dto;

public class LeaderboardDTO {
    private int rank;
    private String name;
    private int skillScore;
    private int quizzesTaken;
    private double accuracy;

    public LeaderboardDTO(int rank, String name, int skillScore, int quizzesTaken, double accuracy) {
        this.rank = rank;
        this.name = name;
        this.skillScore = skillScore;
        this.quizzesTaken = quizzesTaken;
        this.accuracy = accuracy;
    }

    public int getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public int getSkillScore() {
        return skillScore;
    }

    public int getQuizzesTaken() {
        return quizzesTaken;
    }

    public double getAccuracy() {
        return accuracy;
    }
}
