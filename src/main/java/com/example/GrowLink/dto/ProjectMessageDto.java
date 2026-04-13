package com.example.GrowLink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProjectMessageDto {

    @NotBlank(message = "Message cannot be empty.")
    @Size(max = 1000, message = "Message must be under 1000 characters.")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}