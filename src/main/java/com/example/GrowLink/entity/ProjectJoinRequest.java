package com.example.GrowLink.entity;

import com.example.GrowLink.enums.RequestStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "project_join_requests")
public class ProjectJoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Project project;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    // getters & setters
}