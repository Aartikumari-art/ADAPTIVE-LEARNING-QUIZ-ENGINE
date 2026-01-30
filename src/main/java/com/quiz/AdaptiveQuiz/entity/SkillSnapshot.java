package com.quiz.AdaptiveQuiz.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "skill_snapshots")
public class SkillSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    private double skillScore;

    private LocalDateTime timestamp = LocalDateTime.now();

    // Constructors, Getters, Setters
    public SkillSnapshot() {
    }

    public SkillSnapshot(User user, Subject subject, double skillScore) {
        this.user = user;
        this.subject = subject;
        this.skillScore = skillScore;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public double getSkillScore() {
        return skillScore;
    }

    public void setSkillScore(double skillScore) {
        this.skillScore = skillScore;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
