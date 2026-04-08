package com.example.GrowLink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.GrowLink.entity.Message;
import com.example.GrowLink.entity.User;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderOrReceiverOrderBySentAtDesc(User sender, User receiver);

    List<Message> findBySenderAndReceiverOrSenderAndReceiverOrderBySentAtAsc(
            User sender1,
            User receiver1,
            User sender2,
            User receiver2
    );
}