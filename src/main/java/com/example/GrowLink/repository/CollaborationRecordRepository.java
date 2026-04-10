package com.example.GrowLink.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.GrowLink.entity.CollaborationRecord;
import com.example.GrowLink.entity.User;

@Repository
public interface CollaborationRecordRepository extends JpaRepository<CollaborationRecord, Long> {

    Optional<CollaborationRecord> findByUserOneAndUserTwo(User userOne, User userTwo);

    Optional<CollaborationRecord> findByUserOneAndUserTwoOrUserOneAndUserTwo(
            User userOne, User userTwo, User reverseUserOne, User reverseUserTwo
    );
}