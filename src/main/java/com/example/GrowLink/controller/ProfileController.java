package com.example.GrowLink.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.GrowLink.dto.ProfileUpdateDto;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.service.SkillService;
import com.example.GrowLink.service.UserService;

import jakarta.validation.Valid;

@Controller
public class ProfileController {

    private final UserService userService;
    private final SkillService skillService;

    public ProfileController(UserService userService, SkillService skillService) {
        this.userService = userService;
        this.skillService = skillService;
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model,
                                  Principal principal,
                                  @RequestParam(value = "message", required = false) String message) {
        User user = userService.getUserByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("teachSkills", skillService.getTeachSkillsByUserEmail(principal.getName()));
        model.addAttribute("learnSkills", skillService.getLearnSkillsByUserEmail(principal.getName()));
        model.addAttribute("message", message);
        return "profile/profile";
    }

    @GetMapping("/profile/{userId}")
    public String showPublicProfile(@PathVariable Long userId, Model model) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("teachSkills", skillService.getTeachSkillsByUserEmail(user.getEmail()));
        model.addAttribute("learnSkills", skillService.getLearnSkillsByUserEmail(user.getEmail()));
        return "profile/view-profile";
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

        return "redirect:/profile?message=" +
                URLEncoder.encode("Profile updated successfully.", StandardCharsets.UTF_8);
    }
}