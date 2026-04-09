package com.example.GrowLink.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.GrowLink.entity.Review;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getReviewsForUser(User reviewedUser) {
        return reviewRepository.findByReviewedUserOrderByCreatedAtDesc(reviewedUser);
    }

    public Optional<Review> getReviewByReviewerAndReviewed(User reviewer, User reviewedUser) {
        return reviewRepository.findByReviewerAndReviewedUser(reviewer, reviewedUser);
    }

    public boolean hasReviewed(User reviewer, User reviewedUser) {
        return reviewRepository.existsByReviewerAndReviewedUser(reviewer, reviewedUser);
    }

    public Review saveOrUpdateReview(User reviewer, User reviewedUser, Integer rating, String comment) {
        Optional<Review> existingReview = reviewRepository.findByReviewerAndReviewedUser(reviewer, reviewedUser);

        Review review = existingReview.orElseGet(Review::new);
        review.setReviewer(reviewer);
        review.setReviewedUser(reviewedUser);
        review.setRating(rating);
        review.setComment(comment);

        return reviewRepository.save(review);
    }

    public long getReviewCount(User reviewedUser) {
        return reviewRepository.countByReviewedUser(reviewedUser);
    }

    public double getAverageRating(User reviewedUser) {
        Double avg = reviewRepository.findAverageRatingByReviewedUser(reviewedUser);
        return avg != null ? avg : 0.0;
    }
}