package com.example.GrowLink.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(
        name = "collaboration_records",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_one_id", "user_two_id"})
        }
)
public class CollaborationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_one_id", nullable = false)
    private User userOne;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_two_id", nullable = false)
    private User userTwo;

    @Column(name = "user_one_confirmed", nullable = false)
    private boolean userOneConfirmed = false;

    @Column(name = "user_two_confirmed", nullable = false)
    private boolean userTwoConfirmed = false;

    @Column(length = 500)
    private String note;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public CollaborationRecord() {
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUserOne() {
        return userOne;
    }

    public void setUserOne(User userOne) {
        this.userOne = userOne;
    }

    public User getUserTwo() {
        return userTwo;
    }

    public void setUserTwo(User userTwo) {
        this.userTwo = userTwo;
    }

    public boolean isUserOneConfirmed() {
        return userOneConfirmed;
    }

    public void setUserOneConfirmed(boolean userOneConfirmed) {
        this.userOneConfirmed = userOneConfirmed;
    }

    public boolean isUserTwoConfirmed() {
        return userTwoConfirmed;
    }

    public void setUserTwoConfirmed(boolean userTwoConfirmed) {
        this.userTwoConfirmed = userTwoConfirmed;
    }

    public boolean isFullyConfirmed() {
        return userOneConfirmed && userTwoConfirmed;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}