package com.example.GrowLink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProjectDto {

    @NotBlank(message = "Project title is required")
    @Size(max = 150, message = "Project title must not exceed 150 characters")
    private String title;

    @NotBlank(message = "Project description is required")
    @Size(max = 3000, message = "Project description must not exceed 3000 characters")
    private String description;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @Size(max = 500, message = "Required skills must not exceed 500 characters")
    private String requiredSkillsText;

    public ProjectDto() {
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getRequiredSkillsText() {
        return requiredSkillsText;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setRequiredSkillsText(String requiredSkillsText) {
        this.requiredSkillsText = requiredSkillsText;
    }
}