package com.example.GrowLink.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.GrowLink.entity.CollaborationRecord;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.service.CollaborationRecordService;
import com.example.GrowLink.service.ConnectionService;
import com.example.GrowLink.service.UserService;

@Controller
public class CollaborationController {

    private final CollaborationRecordService collaborationRecordService;
    private final ConnectionService connectionService;
    private final UserService userService;

    public CollaborationController(CollaborationRecordService collaborationRecordService,
                                   ConnectionService connectionService,
                                   UserService userService) {
        this.collaborationRecordService = collaborationRecordService;
        this.connectionService = connectionService;
        this.userService = userService;
    }

    @PostMapping("/collaborations/complete")
    public String confirmCollaboration(@RequestParam("otherUserId") Long otherUserId,
                                       @RequestParam(value = "note", required = false) String note,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please log in first.");
            return "redirect:/login";
        }

        User loggedUser = userService.getUserByEmail(authentication.getName());
        User otherUser;

        try {
            otherUser = userService.getUserById(otherUserId);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/";
        }

        if (loggedUser.getId().equals(otherUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid collaboration target.");
            return "redirect:/profile/" + otherUserId;
        }

        if (!connectionService.areConnected(loggedUser, otherUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be connected first.");
            return "redirect:/profile/" + otherUserId;
        }

        if (note != null && note.length() > 500) {
            redirectAttributes.addFlashAttribute("errorMessage", "Note must be less than 500 characters.");
            return "redirect:/profile/" + otherUserId;
        }

        boolean alreadyConfirmed = collaborationRecordService.hasUserConfirmed(loggedUser, otherUser);

        if (alreadyConfirmed) {
            redirectAttributes.addFlashAttribute("errorMessage", "You already confirmed this collaboration.");
            return "redirect:/profile/" + otherUserId;
        }

        CollaborationRecord record = collaborationRecordService.confirmCollaboration(loggedUser, otherUser, note);

        if (record.isFullyConfirmed()) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Both users confirmed collaboration. Reviews are now unlocked.");
        } else {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Your confirmation was saved. Waiting for the other user to confirm.");
        }

        return "redirect:/profile/" + otherUserId;
    }
}