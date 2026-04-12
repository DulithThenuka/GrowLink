package com.example.GrowLink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.GrowLink.entity.ConnectionRequest;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.RequestStatus;

@Repository
public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {

    List<ConnectionRequest> findBySender(User sender);

    List<ConnectionRequest> findByReceiver(User receiver);

    boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, RequestStatus status);

    @Query("""
            SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END
            FROM ConnectionRequest cr
            WHERE (
                (cr.sender.id = :userId1 AND cr.receiver.id = :userId2)
                OR
                (cr.sender.id = :userId2 AND cr.receiver.id = :userId1)
            )
            AND cr.status = :status
            """)
    boolean existsAcceptedConnection(Long userId1, Long userId2, RequestStatus status);

    @Query("""
            SELECT cr
            FROM ConnectionRequest cr
            WHERE (
                cr.sender.id = :userId
                OR
                cr.receiver.id = :userId
            )
            AND cr.status = :status
            """)
    List<ConnectionRequest> findAcceptedConnections(Long userId, RequestStatus status);
}