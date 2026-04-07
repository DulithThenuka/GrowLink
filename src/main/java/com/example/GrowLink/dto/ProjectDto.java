package com.example.GrowLink.dto;

import jakarta.validation.constraints.NotBlank;

public class ProjectDto {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private String category;

    // getters & setters
}