package com.example.GrowLink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.GrowLink.entity.Review;
import com.example.GrowLink.entity.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByReviewedUserOrderByCreatedAtDesc(User reviewedUser);

    Optional<Review> findByReviewerAndReviewedUser(User reviewer, User reviewedUser);

    boolean existsByReviewerAndReviewedUser(User reviewer, User reviewedUser);

    long countByReviewedUser(User reviewedUser);

    void deleteByReviewerAndReviewedUser(User reviewer, User reviewedUser);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewedUser = :reviewedUser")
    Double findAverageRatingByReviewedUser(User reviewedUser);
}