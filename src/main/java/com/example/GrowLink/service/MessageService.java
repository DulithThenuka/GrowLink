package com.example.GrowLink.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.GrowLink.entity.Message;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.repository.MessageRepository;
import com.example.GrowLink.repository.UserRepository;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository,
                          UserService userService,
                          UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public List<User> getAllOtherUsers(String currentEmail) {
        User currentUser = userService.getUserByEmail(currentEmail);

        return userRepository.findAll()
                .stream()
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .toList();
    }

    public List<Message> getConversation(String currentEmail, Long otherUserId) {
        User currentUser = userService.getUserByEmail(currentEmail);
        User otherUser = userRepository.findById(otherUserId).orElse(null);

        if (otherUser == null) {
            return new ArrayList<>();
        }

        return messageRepository.findBySenderAndReceiverOrSenderAndReceiverOrderBySentAtAsc(
                currentUser, otherUser,
                otherUser, currentUser
        );
    }

    public List<User> getConversationUsers(String currentEmail) {
        User currentUser = userService.getUserByEmail(currentEmail);
        List<Message> messages = messageRepository.findBySenderOrReceiverOrderBySentAtDesc(currentUser, currentUser);

        Map<Long, User> uniqueUsers = new LinkedHashMap<>();

        for (Message message : messages) {
            User otherUser = message.getSender().getId().equals(currentUser.getId())
                    ? message.getReceiver()
                    : message.getSender();

            uniqueUsers.putIfAbsent(otherUser.getId(), otherUser);
        }

        return new ArrayList<>(uniqueUsers.values());
    }

    @Transactional
    public String sendMessage(String senderEmail, Long receiverId, String text) {
        if (text == null || text.trim().isEmpty()) {
            return "Message cannot be empty.";
        }

        User sender = userService.getUserByEmail(senderEmail);
        User receiver = userRepository.findById(receiverId).orElse(null);

        if (receiver == null) {
            return "Receiver not found.";
        }

        if (sender.getId().equals(receiver.getId())) {
            return "You cannot message yourself.";
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMessageText(text.trim());
        message.setIsRead(false);

        messageRepository.save(message);
        return "Message sent successfully.";
    }
}