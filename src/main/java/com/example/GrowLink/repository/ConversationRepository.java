package com.example.GrowLink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.GrowLink.entity.Conversation;
import com.example.GrowLink.entity.User;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByUser1OrUser2OrderByUpdatedAtDesc(User user1, User user2);

    Optional<Conversation> findByUser1AndUser2(User user1, User user2);
}