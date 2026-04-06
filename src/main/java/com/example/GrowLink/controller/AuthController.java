package com.example.GrowLink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.GrowLink.dto.RegisterDto;
import com.example.GrowLink.service.UserService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerDto") RegisterDto registerDto,
                               BindingResult bindingResult,
                               Model model) {

        if (userService.emailExists(registerDto.getEmail())) {
            bindingResult.rejectValue("email", "error.registerDto", "Email is already registered");
        }

        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.registerDto", "Passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        userService.registerUser(registerDto);
        model.addAttribute("successMessage", "Registration successful. Please login.");
        model.addAttribute("registerDto", new RegisterDto());

        return "auth/register";
    }
}