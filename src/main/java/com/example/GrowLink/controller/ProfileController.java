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
import com.example.GrowLink.service.ReviewService;
import com.example.GrowLink.service.UserService;

@Controller
public class ProfileController {

    private final UserService userService;
    private final ReviewService reviewService;

    public ProfileController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping("/profile/{id}")
    public String viewProfile(@PathVariable Long id, Authentication authentication, Model model) {
        User profileUser;

        try {
            profileUser = userService.getUserById(id);
        } catch (IllegalArgumentException e) {
            return "redirect:/";
        }

        User loggedUser = null;
        Review myReview = null;

        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            try {
                loggedUser = userService.getUserByEmail(authentication.getName());

                if (loggedUser != null && !loggedUser.getId().equals(profileUser.getId())) {
                    Optional<Review> existing = reviewService.getReviewByReviewerAndReviewed(loggedUser, profileUser);
                    if (existing.isPresent()) {
                        myReview = existing.get();
                    }
                }
            } catch (Exception e) {
                loggedUser = null;
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

        return "profile/view";
    }
}