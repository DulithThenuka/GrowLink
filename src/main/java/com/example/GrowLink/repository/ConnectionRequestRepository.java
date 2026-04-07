package com.example.GrowLink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.GrowLink.entity.ConnectionRequest;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.RequestStatus;

@Repository
public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {

    List<ConnectionRequest> findBySender(User sender);

    List<ConnectionRequest> findByReceiver(User receiver);

    List<ConnectionRequest> findBySenderOrReceiverAndStatus(User sender, User receiver, RequestStatus status);

    Optional<ConnectionRequest> findBySenderAndReceiver(User sender, User receiver);

    Optional<ConnectionRequest> findByIdAndReceiver(Long id, User receiver);
}