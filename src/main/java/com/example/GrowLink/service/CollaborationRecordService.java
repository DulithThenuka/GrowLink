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
                .map(CollaborationRecord::isFullyConfirmed)
                .orElse(false);
    }

    public boolean hasUserConfirmed(User currentUser, User otherUser) {
        Optional<CollaborationRecord> optionalRecord = getRecord(currentUser, otherUser);

        if (optionalRecord.isEmpty()) {
            return false;
        }

        CollaborationRecord record = optionalRecord.get();

        if (record.getUserOne().getId().equals(currentUser.getId())) {
            return record.isUserOneConfirmed();
        }

        if (record.getUserTwo().getId().equals(currentUser.getId())) {
            return record.isUserTwoConfirmed();
        }

        return false;
    }

    @Transactional
    public CollaborationRecord confirmCollaboration(User currentUser, User otherUser, String note) {
        CollaborationRecord record = getRecord(currentUser, otherUser).orElseGet(CollaborationRecord::new);

        if (record.getId() == null) {
            if (currentUser.getId() < otherUser.getId()) {
                record.setUserOne(currentUser);
                record.setUserTwo(otherUser);
            } else {
                record.setUserOne(otherUser);
                record.setUserTwo(currentUser);
            }
        }

        if (record.getUserOne().getId().equals(currentUser.getId())) {
            record.setUserOneConfirmed(true);
        } else if (record.getUserTwo().getId().equals(currentUser.getId())) {
            record.setUserTwoConfirmed(true);
        }

        if (note != null && !note.trim().isEmpty()) {
            record.setNote(note.trim());
        }

        return collaborationRecordRepository.save(record);
    }
}