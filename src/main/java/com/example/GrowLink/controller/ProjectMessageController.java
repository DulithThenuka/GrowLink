package com.example.GrowLink.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.GrowLink.dto.ProjectMessageDto;
import com.example.GrowLink.entity.Project;
import com.example.GrowLink.service.ProjectMessageService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/projects/{projectId}/chat")
public class ProjectMessageController {

    private final ProjectMessageService projectMessageService;

    public ProjectMessageController(ProjectMessageService projectMessageService) {
        this.projectMessageService = projectMessageService;
    }

    @GetMapping
    public String showProjectChatPage(@PathVariable Long projectId,
                                      Model model,
                                      Principal principal,
                                      @RequestParam(value = "message", required = false) String message) {

        Project project = projectMessageService.getProjectById(projectId);

        boolean isOwner = false;
        boolean isMember = false;

        if (principal != null) {
            isOwner = projectMessageService.isOwner(principal.getName(), projectId);
            isMember = projectMessageService.isMember(principal.getName(), projectId);
        }

        if (!isOwner && !isMember) {
            return "redirect:/projects/" + projectId + "?message=" +
                    URLEncoder.encode("You do not have access to the project chat.", StandardCharsets.UTF_8);
        }

        model.addAttribute("project", project);
        model.addAttribute("messages", projectMessageService.getMessagesByProjectId(projectId));
        model.addAttribute("messageDto", new ProjectMessageDto());
        model.addAttribute("messageCount", projectMessageService.getMessageCount(projectId));
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isMember", isMember);
        model.addAttribute("message", message);

        return "projects/project-chat";
    }

    @PostMapping("/send")
    public String sendMessage(@PathVariable Long projectId,
                              @Valid @ModelAttribute("messageDto") ProjectMessageDto messageDto,
                              BindingResult bindingResult,
                              Principal principal,
                              Model model) {

        if (bindingResult.hasErrors()) {
            Project project = projectMessageService.getProjectById(projectId);

            model.addAttribute("project", project);
            model.addAttribute("messages", projectMessageService.getMessagesByProjectId(projectId));
            model.addAttribute("messageCount", projectMessageService.getMessageCount(projectId));
            model.addAttribute("isOwner", projectMessageService.isOwner(principal.getName(), projectId));
            model.addAttribute("isMember", projectMessageService.isMember(principal.getName(), projectId));

            return "projects/project-chat";
        }

        String message = projectMessageService.sendMessage(projectId, principal.getName(), messageDto);

        return "redirect:/projects/" + projectId + "/chat?message=" +
                URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    @PostMapping("/{messageId}/delete")
    public String deleteMessage(@PathVariable Long projectId,
                                @PathVariable Long messageId,
                                Principal principal) {

        String message = projectMessageService.deleteMessage(projectId, messageId, principal.getName());

        return "redirect:/projects/" + projectId + "/chat?message=" +
                URLEncoder.encode(message, StandardCharsets.UTF_8);
    }
}