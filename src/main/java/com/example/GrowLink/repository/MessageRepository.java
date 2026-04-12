package com.example.GrowLink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.GrowLink.entity.Conversation;
import com.example.GrowLink.entity.Message;
import com.example.GrowLink.entity.User;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);

    Optional<Message> findTopByConversationOrderByCreatedAtDesc(Conversation conversation);

    long countByConversationAndSenderNotAndIsReadFalse(Conversation conversation, User sender);

    List<Message> findByConversationAndSenderNotAndIsReadFalse(Conversation conversation, User sender);
}