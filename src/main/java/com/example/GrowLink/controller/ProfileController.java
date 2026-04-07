package com.example.GrowLink.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.GrowLink.dto.ProfileUpdateDto;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.service.UserService;

import jakarta.validation.Valid;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        model.addAttribute("user", user);
        return "profile/profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfilePage(Model model, Principal principal) {
        ProfileUpdateDto profileUpdateDto = userService.getProfileUpdateDtoByEmail(principal.getName());
        model.addAttribute("profileUpdateDto", profileUpdateDto);
        return "profile/edit-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@Valid @ModelAttribute("profileUpdateDto") ProfileUpdateDto profileUpdateDto,
                                BindingResult bindingResult,
                                Principal principal,
                                Model model) {

        if (bindingResult.hasErrors()) {
            return "profile/edit-profile";
        }

        userService.updateProfile(principal.getName(), profileUpdateDto);
        model.addAttribute("successMessage", "Profile updated successfully.");
        model.addAttribute("profileUpdateDto", userService.getProfileUpdateDtoByEmail(principal.getName()));

        return "profile/edit-profile";
    }
}