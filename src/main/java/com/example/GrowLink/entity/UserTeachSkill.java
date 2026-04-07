package com.example.GrowLink.entity;

import com.example.GrowLink.enums.SkillLevel;

import jakarta.persistence.*;

@Entity
@Table(name = "user_teach_skills")
public class UserTeachSkill {

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
    @Column(nullable = false, length = 30)
    private SkillLevel level;

    @Column(name = "experience_text", length = 255)
    private String experienceText;

    public UserTeachSkill() {
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

    public SkillLevel getLevel() {
        return level;
    }

    public String getExperienceText() {
        return experienceText;
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

    public void setLevel(SkillLevel level) {
        this.level = level;
    }

    public void setExperienceText(String experienceText) {
        this.experienceText = experienceText;
    }
}