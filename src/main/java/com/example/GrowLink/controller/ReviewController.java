package com.example.GrowLink.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.GrowLink.entity.User;
import com.example.GrowLink.service.ReviewService;
import com.example.GrowLink.service.UserService;

@Controller
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @PostMapping("/reviews/save")
    public String saveReview(@RequestParam("reviewedUserId") Long reviewedUserId,
                             @RequestParam("rating") Integer rating,
                             @RequestParam("comment") String comment,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please log in first.");
            return "redirect:/login";
        }

        User reviewer;
        User reviewedUser;

        try {
            reviewer = userService.getUserByEmail(authentication.getName());
            reviewedUser = userService.getUserById(reviewedUserId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/";
        }

        if (reviewer.getId().equals(reviewedUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot review yourself.");
            return "redirect:/profile/" + reviewedUserId;
        }

        if (rating == null || rating < 1 || rating > 5) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rating must be between 1 and 5.");
            return "redirect:/profile/" + reviewedUserId;
        }

        reviewService.saveOrUpdateReview(reviewer, reviewedUser, rating, comment);
        redirectAttributes.addFlashAttribute("successMessage", "Review saved successfully.");

        return "redirect:/profile/" + reviewedUserId;
    }
}