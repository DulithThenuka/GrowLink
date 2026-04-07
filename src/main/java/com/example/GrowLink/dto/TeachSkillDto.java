package com.example.GrowLink.dto;

import com.example.GrowLink.enums.SkillLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TeachSkillDto {

    @NotBlank(message = "Skill name is required")
    @Size(max = 100, message = "Skill name must not exceed 100 characters")
    private String skillName;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @NotNull(message = "Skill level is required")
    private SkillLevel level;

    @Size(max = 255, message = "Experience text must not exceed 255 characters")
    private String experienceText;

    public TeachSkillDto() {
    }

    public String getSkillName() {
        return skillName;
    }

    public String getCategory() {
        return category;
    }

    public SkillLevel getLevel() {
        return level;
    }

    public String getExperienceText() {
        return experienceText;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLevel(SkillLevel level) {
        this.level = level;
    }

    public void setExperienceText(String experienceText) {
        this.experienceText = experienceText;
    }
}