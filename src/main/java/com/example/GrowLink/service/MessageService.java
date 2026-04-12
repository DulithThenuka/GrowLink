package com.example.GrowLink.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.GrowLink.entity.Conversation;
import com.example.GrowLink.entity.Message;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.NotificationType;
import com.example.GrowLink.repository.ConversationRepository;
import com.example.GrowLink.repository.MessageRepository;

@Service
public class MessageService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public MessageService(ConversationRepository conversationRepository,
                          MessageRepository messageRepository,
                          UserService userService,
                          NotificationService notificationService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    public List<Conversation> getUserConversations(String email) {
        User user = userService.getUserByEmail(email);
        return conversationRepository.findByUser1OrUser2OrderByUpdatedAtDesc(user, user);
    }

    public Conversation getConversationById(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found."));
    }

    public List<Message> getMessagesByConversation(Long conversationId) {
        Conversation conversation = getConversationById(conversationId);
        return messageRepository.findByConversationOrderByCreatedAtAsc(conversation);
    }

    public User getOtherUser(Conversation conversation, String currentUserEmail) {
        if (conversation.getUser1().getEmail().equals(currentUserEmail)) {
            return conversation.getUser2();
        }
        return conversation.getUser1();
    }

    public boolean isUserInConversation(Long conversationId, String email) {
        Conversation conversation = getConversationById(conversationId);
        return conversation.getUser1().getEmail().equals(email)
                || conversation.getUser2().getEmail().equals(email);
    }

    public Optional<Conversation> findConversationBetweenUsers(User userA, User userB) {
        Optional<Conversation> direct = conversationRepository.findByUser1AndUser2(userA, userB);
        if (direct.isPresent()) {
            return direct;
        }
        return conversationRepository.findByUser1AndUser2(userB, userA);
    }

    @Transactional
    public Conversation startConversation(String currentUserEmail, Long otherUserId) {
        User currentUser = userService.getUserByEmail(currentUserEmail);
        User otherUser = userService.getUserById(otherUserId);

        if (currentUser.getId().equals(otherUser.getId())) {
            throw new IllegalArgumentException("You cannot start a conversation with yourself.");
        }

        Optional<Conversation> existing = findConversationBetweenUsers(currentUser, otherUser);
        if (existing.isPresent()) {
            return existing.get();
        }

        Conversation conversation = new Conversation();
        conversation.setUser1(currentUser);
        conversation.setUser2(otherUser);
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());

        return conversationRepository.save(conversation);
    }

    @Transactional
    public void sendMessage(Long conversationId, String senderEmail, String content) {
        if (content == null || content.isBlank()) {
            return;
        }

        Conversation conversation = getConversationById(conversationId);
        User sender = userService.getUserByEmail(senderEmail);

        if (!conversation.getUser1().getId().equals(sender.getId())
                && !conversation.getUser2().getId().equals(sender.getId())) {
            throw new IllegalArgumentException("You are not allowed to send messages in this conversation.");
        }

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content.trim());
        message.setCreatedAt(LocalDateTime.now());

        messageRepository.save(message);

        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        User receiver = conversation.getUser1().getId().equals(sender.getId())
                ? conversation.getUser2()
                : conversation.getUser1();

        notificationService.createNotification(
                receiver,
                "New Message",
                sender.getFullName() + " sent you a message.",
                NotificationType.MESSAGE
        );
    }
}