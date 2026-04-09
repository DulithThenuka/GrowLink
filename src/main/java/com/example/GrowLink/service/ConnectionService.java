package com.example.GrowLink.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.GrowLink.entity.ConnectionRequest;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.NotificationType;
import com.example.GrowLink.enums.RequestStatus;
import com.example.GrowLink.repository.ConnectionRequestRepository;
import com.example.GrowLink.repository.UserRepository;

@Service
public class ConnectionService {

    private final ConnectionRequestRepository connectionRequestRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ConnectionService(ConnectionRequestRepository connectionRequestRepository,
                             UserService userService,
                             UserRepository userRepository,
                             NotificationService notificationService) {
        this.connectionRequestRepository = connectionRequestRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public List<User> getOtherUsers(String currentEmail) {
        User currentUser = userService.getUserByEmail(currentEmail);

        return userRepository.findAll()
                .stream()
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .toList();
    }

    public List<ConnectionRequest> getSentRequests(String email) {
        User user = userService.getUserByEmail(email);
        return connectionRequestRepository.findBySender(user);
    }

    public List<ConnectionRequest> getReceivedRequests(String email) {
        User user = userService.getUserByEmail(email);
        return connectionRequestRepository.findByReceiver(user);
    }

    public List<User> getAcceptedConnections(String email) {
        User currentUser = userService.getUserByEmail(email);

        List<ConnectionRequest> acceptedRequests =
                connectionRequestRepository.findBySenderOrReceiverAndStatus(
                        currentUser,
                        currentUser,
                        RequestStatus.ACCEPTED
                );

        List<User> connectedUsers = new ArrayList<>();

        for (ConnectionRequest request : acceptedRequests) {
            if (request.getSender().getId().equals(currentUser.getId())) {
                connectedUsers.add(request.getReceiver());
            } else {
                connectedUsers.add(request.getSender());
            }
        }

        return connectedUsers;
    }

    @Transactional
    public String sendConnectionRequest(String senderEmail, Long receiverId) {
        User sender = userService.getUserByEmail(senderEmail);
        User receiver = userRepository.findById(receiverId).orElse(null);

        if (receiver == null) {
            return "User not found.";
        }

        if (sender.getId().equals(receiver.getId())) {
            return "You cannot connect with yourself.";
        }

        if (connectionRequestRepository.findBySenderAndReceiver(sender, receiver).isPresent()
                || connectionRequestRepository.findBySenderAndReceiver(receiver, sender).isPresent()) {
            return "Connection request already exists between these users.";
        }

        ConnectionRequest request = new ConnectionRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        connectionRequestRepository.save(request);

        notificationService.createNotification(
                receiver,
                "New Connection Request",
                sender.getFullName() + " sent you a connection request.",
                NotificationType.CONNECTION
        );

        return "Connection request sent successfully.";
    }

    @Transactional
    public String acceptRequest(String receiverEmail, Long requestId) {
        User receiver = userService.getUserByEmail(receiverEmail);

        ConnectionRequest request = connectionRequestRepository.findByIdAndReceiver(requestId, receiver)
                .orElse(null);

        if (request == null) {
            return "Request not found.";
        }

        request.setStatus(RequestStatus.ACCEPTED);
        connectionRequestRepository.save(request);

        notificationService.createNotification(
                request.getSender(),
                "Connection Accepted",
                receiver.getFullName() + " accepted your connection request.",
                NotificationType.CONNECTION
        );

        return "Connection request accepted.";
    }

    @Transactional
    public String rejectRequest(String receiverEmail, Long requestId) {
        User receiver = userService.getUserByEmail(receiverEmail);

        ConnectionRequest request = connectionRequestRepository.findByIdAndReceiver(requestId, receiver)
                .orElse(null);

        if (request == null) {
            return "Request not found.";
        }

        request.setStatus(RequestStatus.REJECTED);
        connectionRequestRepository.save(request);

        return "Connection request rejected.";
    }
    public boolean areConnected(User user1, User user2) {
    if (user1 == null || user2 == null) {
        return false;
    }

    return connectionRepository.findAll().stream().anyMatch(connection ->
            "ACCEPTED".equalsIgnoreCase(connection.getStatus().name()) &&
            (
                (connection.getRequester().getId().equals(user1.getId()) &&
                 connection.getReceiver().getId().equals(user2.getId()))
             ||
                (connection.getRequester().getId().equals(user2.getId()) &&
                 connection.getReceiver().getId().equals(user1.getId()))
            )
    );
}
}