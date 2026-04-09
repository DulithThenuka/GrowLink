package com.example.GrowLink.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProfileUpdateDto {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @Size(max = 150, message = "Headline must not exceed 150 characters")
    private String headline;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;

    private MultipartFile profileImageFile;

    public ProfileUpdateDto() {
    }

    public String getFullName() {
        return fullName;
    }

    public String getHeadline() {
        return headline;
    }

    public String getLocation() {
        return location;
    }

    public String getBio() {
        return bio;
    }

    public MultipartFile getProfileImageFile() {
        return profileImageFile;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setProfileImageFile(MultipartFile profileImageFile) {
        this.profileImageFile = profileImageFile;
    }
}