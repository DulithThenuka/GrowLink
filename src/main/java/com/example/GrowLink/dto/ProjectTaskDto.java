package com.example.GrowLink.dto;

import java.time.LocalDate;

import com.example.GrowLink.enums.TaskPriority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProjectTaskDto {

    @NotBlank(message = "Task title is required.")
    @Size(max = 150, message = "Task title must be under 150 characters.")
    private String title;

    @Size(max = 1000, message = "Description must be under 1000 characters.")
    private String description;

    private TaskPriority priority;

    private LocalDate dueDate;

    private Long assignedUserId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }
}