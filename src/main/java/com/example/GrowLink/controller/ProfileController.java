package com.example.GrowLink.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.GrowLink.entity.Review;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.service.CollaborationRecordService;
import com.example.GrowLink.service.ConnectionService;
import com.example.GrowLink.service.ReviewService;
import com.example.GrowLink.service.UserService;

@Controller
public class ProfileController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final ConnectionService connectionService;
    private final CollaborationRecordService collaborationRecordService;

    public ProfileController(UserService userService,
                             ReviewService reviewService,
                             ConnectionService connectionService,
                             CollaborationRecordService collaborationRecordService) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.connectionService = connectionService;
        this.collaborationRecordService = collaborationRecordService;
    }

    @GetMapping("/profile/{id}")
    public String viewProfile(@PathVariable Long id, Authentication authentication, Model model) {
        User profileUser;

        try {
            profileUser = userService.getUserById(id);
        } catch (IllegalArgumentException ex) {
            return "redirect:/";
        }

        User loggedUser = null;
        Review myReview = null;
        boolean connected = false;
        boolean currentUserConfirmed = false;
        boolean otherUserConfirmed = false;
        boolean collaborationCompleted = false;
        boolean canReview = false;

        if (authentication != null && authentication.isAuthenticated()) {
            loggedUser = userService.getUserByEmail(authentication.getName());

            if (!loggedUser.getId().equals(profileUser.getId())) {
                connected = connectionService.areConnected(loggedUser, profileUser);

                if (connected) {
                    currentUserConfirmed = collaborationRecordService.hasUserConfirmed(loggedUser, profileUser);
                    otherUserConfirmed = collaborationRecordService.hasUserConfirmed(profileUser, loggedUser);
                    collaborationCompleted = collaborationRecordService.hasCompletedCollaboration(loggedUser, profileUser);
                    canReview = collaborationCompleted;
                }

                Optional<Review> existing = reviewService.getReviewByReviewerAndReviewed(loggedUser, profileUser);
                if (existing.isPresent()) {
                    myReview = existing.get();
                }
            }
        }

        List<Review> reviews = reviewService.getReviewsForUser(profileUser);
        double averageRating = reviewService.getAverageRating(profileUser);
        long reviewCount = reviewService.getReviewCount(profileUser);

        model.addAttribute("profileUser", profileUser);
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("myReview", myReview);
        model.addAttribute("connected", connected);
        model.addAttribute("currentUserConfirmed", currentUserConfirmed);
        model.addAttribute("otherUserConfirmed", otherUserConfirmed);
        model.addAttribute("collaborationCompleted", collaborationCompleted);
        model.addAttribute("canReview", canReview);

        return "profile/view-profile";
    }
}