package com.example.GrowLink.entity;

import java.time.LocalDateTime;

import com.example.GrowLink.enums.RequestStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "learning_requests")
public class LearningRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "learner_id", nullable = false)
    private User learner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public LearningRequest() {
    }

    @PrePersist
    public void onCreate() {
        this.requestedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getLearner() {
        return learner;
    }

    public User getTeacher() {
        return teacher;
    }

    public Skill getSkill() {
        return skill;
    }

    public String getMessage() {
        return message;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLearner(User learner) {
        this.learner = learner;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}