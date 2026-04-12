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
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ConnectionService(ConnectionRequestRepository connectionRequestRepository,
                             UserRepository userRepository,
                             NotificationService notificationService) {
        this.connectionRequestRepository = connectionRequestRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public String sendConnectionRequest(String senderEmail, Long receiverId) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found."));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found."));

        if (sender.getId().equals(receiver.getId())) {
            return "You cannot send a connection request to yourself.";
        }

        if (areConnected(sender, receiver)) {
            return "You are already connected with this user.";
        }

        if (hasPendingRequestBetweenUsers(sender, receiver)) {
            return "A pending connection request already exists.";
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

    public List<ConnectionRequest> getSentRequests(String email) {
        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return connectionRequestRepository.findBySender(sender);
    }

    public List<ConnectionRequest> getReceivedRequests(String email) {
        User receiver = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return connectionRequestRepository.findByReceiver(receiver);
    }

    public List<User> getAcceptedConnections(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        List<ConnectionRequest> acceptedRequests =
                connectionRequestRepository.findAcceptedConnections(user.getId(), RequestStatus.ACCEPTED);

        List<User> connections = new ArrayList<>();

        for (ConnectionRequest request : acceptedRequests) {
            if (request.getSender().getId().equals(user.getId())) {
                connections.add(request.getReceiver());
            } else {
                connections.add(request.getSender());
            }
        }

        return connections;
    }

    @Transactional
    public String acceptRequest(Long requestId, String receiverEmail) {
        ConnectionRequest request = connectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Connection request not found."));

        if (!request.getReceiver().getEmail().equals(receiverEmail)) {
            return "You are not allowed to accept this request.";
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            return "This request has already been processed.";
        }

        request.setStatus(RequestStatus.ACCEPTED);
        connectionRequestRepository.save(request);

        notificationService.createNotification(
                request.getSender(),
                "Connection Request Accepted",
                request.getReceiver().getFullName() + " accepted your connection request.",
                NotificationType.CONNECTION
        );

        return "Connection request accepted.";
    }

    @Transactional
    public String rejectRequest(Long requestId, String receiverEmail) {
        ConnectionRequest request = connectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Connection request not found."));

        if (!request.getReceiver().getEmail().equals(receiverEmail)) {
            return "You are not allowed to reject this request.";
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            return "This request has already been processed.";
        }

        request.setStatus(RequestStatus.REJECTED);
        connectionRequestRepository.save(request);

        notificationService.createNotification(
                request.getSender(),
                "Connection Request Rejected",
                request.getReceiver().getFullName() + " rejected your connection request.",
                NotificationType.CONNECTION
        );

        return "Connection request rejected.";
    }

    public boolean areConnected(User user1, User user2) {
        return connectionRequestRepository.existsAcceptedConnection(
                user1.getId(),
                user2.getId(),
                RequestStatus.ACCEPTED
        );
    }

    public boolean hasPendingRequestFromTo(User sender, User receiver) {
        return connectionRequestRepository.existsBySenderAndReceiverAndStatus(
                sender,
                receiver,
                RequestStatus.PENDING
        );
    }

    public boolean hasPendingRequestBetweenUsers(User user1, User user2) {
        return connectionRequestRepository.existsBySenderAndReceiverAndStatus(user1, user2, RequestStatus.PENDING)
                || connectionRequestRepository.existsBySenderAndReceiverAndStatus(user2, user1, RequestStatus.PENDING);
    }

    public boolean canConnect(String currentUserEmail, Long otherUserId) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new IllegalArgumentException("Other user not found."));

        if (currentUser.getId().equals(otherUser.getId())) {
            return false;
        }

        if (areConnected(currentUser, otherUser)) {
            return false;
        }

        return !hasPendingRequestBetweenUsers(currentUser, otherUser);
    }

    public boolean isPendingBetween(String currentUserEmail, Long otherUserId) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new IllegalArgumentException("Other user not found."));

        return hasPendingRequestBetweenUsers(currentUser, otherUser);
    }
}