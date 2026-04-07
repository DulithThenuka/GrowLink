package com.example.GrowLink.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.GrowLink.dto.LearningRequestDto;
import com.example.GrowLink.service.LearningRequestService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/learning-requests")
public class LearningRequestController {

    private final LearningRequestService learningRequestService;

    public LearningRequestController(LearningRequestService learningRequestService) {
        this.learningRequestService = learningRequestService;
    }

    @GetMapping
    public String showLearningRequestsPage(Model model,
                                           Principal principal,
                                           @RequestParam(value = "message", required = false) String message) {

        if (!model.containsAttribute("learningRequestDto")) {
            model.addAttribute("learningRequestDto", new LearningRequestDto());
        }

        model.addAttribute("availableTeachSkills", learningRequestService.getAllTeachSkills());
        model.addAttribute("sentLearningRequests", learningRequestService.getSentRequests(principal.getName()));
        model.addAttribute("receivedLearningRequests", learningRequestService.getReceivedRequests(principal.getName()));
        model.addAttribute("message", message);

        return "learning/learning-requests";
    }

    @PostMapping("/send")
    public String sendLearningRequest(@Valid @ModelAttribute("learningRequestDto") LearningRequestDto learningRequestDto,
                                      BindingResult bindingResult,
                                      Principal principal,
                                      Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("availableTeachSkills", learningRequestService.getAllTeachSkills());
            model.addAttribute("sentLearningRequests", learningRequestService.getSentRequests(principal.getName()));
            model.addAttribute("receivedLearningRequests", learningRequestService.getReceivedRequests(principal.getName()));
            return "learning/learning-requests";
        }

        String message = learningRequestService.sendLearningRequest(principal.getName(), learningRequestDto);
        return "redirect:/learning-requests?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    @PostMapping("/accept/{requestId}")
    public String acceptRequest(@PathVariable Long requestId, Principal principal) {
        String message = learningRequestService.acceptRequest(principal.getName(), requestId);
        return "redirect:/learning-requests?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    @PostMapping("/reject/{requestId}")
    public String rejectRequest(@PathVariable Long requestId, Principal principal) {
        String message = learningRequestService.rejectRequest(principal.getName(), requestId);
        return "redirect:/learning-requests?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    @PostMapping("/complete/{requestId}")
    public String completeRequest(@PathVariable Long requestId, Principal principal) {
        String message = learningRequestService.completeRequest(principal.getName(), requestId);
        return "redirect:/learning-requests?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }
}