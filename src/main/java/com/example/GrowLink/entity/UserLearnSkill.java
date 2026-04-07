package com.example.GrowLink.entity;

import com.example.GrowLink.enums.SkillLevel;

import jakarta.persistence.*;

@Entity
@Table(name = "user_learn_skills")
public class UserLearnSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_level", nullable = false, length = 30)
    private SkillLevel targetLevel;

    @Column(length = 255)
    private String note;

    public UserLearnSkill() {
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Skill getSkill() {
        return skill;
    }

    public SkillLevel getTargetLevel() {
        return targetLevel;
    }

    public String getNote() {
        return note;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public void setTargetLevel(SkillLevel targetLevel) {
        this.targetLevel = targetLevel;
    }

    public void setNote(String note) {
        this.note = note;
    }
}