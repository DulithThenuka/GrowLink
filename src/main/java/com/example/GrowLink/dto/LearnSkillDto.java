package com.example.GrowLink.dto;

import com.example.GrowLink.enums.SkillLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LearnSkillDto {

    @NotBlank(message = "Skill name is required")
    @Size(max = 100, message = "Skill name must not exceed 100 characters")
    private String skillName;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @NotNull(message = "Target level is required")
    private SkillLevel targetLevel;

    @Size(max = 255, message = "Note must not exceed 255 characters")
    private String note;

    public LearnSkillDto() {
    }

    public String getSkillName() {
        return skillName;
    }

    public String getCategory() {
        return category;
    }

    public SkillLevel getTargetLevel() {
        return targetLevel;
    }

    public String getNote() {
        return note;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTargetLevel(SkillLevel targetLevel) {
        this.targetLevel = targetLevel;
    }

    public void setNote(String note) {
        this.note = note;
    }
}