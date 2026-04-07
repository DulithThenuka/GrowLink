package com.example.GrowLink.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LearningRequestDto {

    @NotNull(message = "Teacher is required")
    private Long teacherId;

    @NotNull(message = "Skill is required")
    private Long skillId;

    @Size(max = 500, message = "Message must not exceed 500 characters")
    private String message;

    public LearningRequestDto() {
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public String getMessage() {
        return message;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}