package com.example.GrowLink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.GrowLink.entity.LearningRequest;
import com.example.GrowLink.entity.User;

@Repository
public interface LearningRequestRepository extends JpaRepository<LearningRequest, Long> {

    List<LearningRequest> findByLearner(User learner);

    List<LearningRequest> findByTeacher(User teacher);

    Optional<LearningRequest> findByIdAndTeacher(Long id, User teacher);
}