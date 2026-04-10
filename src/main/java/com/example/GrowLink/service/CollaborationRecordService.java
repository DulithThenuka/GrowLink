package com.example.GrowLink.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.GrowLink.entity.CollaborationRecord;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.repository.CollaborationRecordRepository;

@Service
public class CollaborationRecordService {

    private final CollaborationRecordRepository collaborationRecordRepository;

    public CollaborationRecordService(CollaborationRecordRepository collaborationRecordRepository) {
        this.collaborationRecordRepository = collaborationRecordRepository;
    }

    public Optional<CollaborationRecord> getRecord(User user1, User user2) {
        return collaborationRecordRepository.findByUserOneAndUserTwoOrUserOneAndUserTwo(
                user1, user2, user2, user1
        );
    }

    public boolean hasCompletedCollaboration(User user1, User user2) {
        return getRecord(user1, user2)
                .map(CollaborationRecord::isCompleted)
                .orElse(false);
    }

    @Transactional
    public CollaborationRecord markCompleted(User user1, User user2, String note) {
        CollaborationRecord record = getRecord(user1, user2).orElseGet(CollaborationRecord::new);

        if (record.getId() == null) {
            if (user1.getId() < user2.getId()) {
                record.setUserOne(user1);
                record.setUserTwo(user2);
            } else {
                record.setUserOne(user2);
                record.setUserTwo(user1);
            }
        }

        record.setCompleted(true);
        record.setNote(note != null ? note.trim() : null);

        return collaborationRecordRepository.save(record);
    }
}