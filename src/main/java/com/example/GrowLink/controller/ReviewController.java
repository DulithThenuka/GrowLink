package com.example.GrowLink.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.GrowLink.entity.User;
import com.example.GrowLink.service.ConnectionService;
import com.example.GrowLink.service.ReviewService;
import com.example.GrowLink.service.UserService;

@Controller
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final ConnectionService connectionService;

    public ReviewController(ReviewService reviewService,
                            UserService userService,
                            ConnectionService connectionService) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.connectionService = connectionService;
    }

    @PostMapping("/reviews/save")
    public String saveReview(@RequestParam("reviewedUserId") Long reviewedUserId,
                             @RequestParam("rating") Integer rating,
                             @RequestParam(value = "comment", required = false) String comment,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please log in first.");
            return "redirect:/login";
        }

        User reviewer = userService.getUserByEmail(authentication.getName());
        User reviewedUser = userService.getUserById(reviewedUserId);

        if (reviewer == null || reviewedUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/";
        }

        if (reviewer.getId().equals(reviewedUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot review yourself.");
            return "redirect:/profile/" + reviewedUserId;
        }

        if (!connectionService.areConnected(reviewer, reviewedUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only review users you are connected with.");
            return "redirect:/profile/" + reviewedUserId;
        }

        if (rating == null || rating < 1 || rating > 5) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rating must be between 1 and 5.");
            return "redirect:/profile/" + reviewedUserId;
        }

        if (comment != null && comment.length() > 1000) {
            redirectAttributes.addFlashAttribute("errorMessage", "Comment must be less than 1000 characters.");
            return "redirect:/profile/" + reviewedUserId;
        }

        reviewService.saveOrUpdateReview(reviewer, reviewedUser, rating, comment);
        redirectAttributes.addFlashAttribute("successMessage", "Review saved successfully.");
        return "redirect:/profile/" + reviewedUserId;
    }

    @PostMapping("/reviews/delete")
    public String deleteReview(@RequestParam("reviewedUserId") Long reviewedUserId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please log in first.");
            return "redirect:/login";
        }

        User reviewer = userService.getUserByEmail(authentication.getName());
        User reviewedUser = userService.getUserById(reviewedUserId);

        if (reviewer == null || reviewedUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/";
        }

        reviewService.deleteReview(reviewer, reviewedUser);
        redirectAttributes.addFlashAttribute("successMessage", "Review deleted successfully.");

        return "redirect:/profile/" + reviewedUserId;
    }
}