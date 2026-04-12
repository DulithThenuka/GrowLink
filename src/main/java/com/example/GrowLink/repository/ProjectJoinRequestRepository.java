package com.example.GrowLink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.ProjectJoinRequest;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.RequestStatus;

public interface ProjectJoinRequestRepository extends JpaRepository<ProjectJoinRequest, Long> {

    List<ProjectJoinRequest> findByProjectOrderByIdDesc(Project project);

    List<ProjectJoinRequest> findByProjectAndStatusOrderByIdDesc(Project project, RequestStatus status);

    Optional<ProjectJoinRequest> findByProjectAndUser(Project project, User user);

    boolean existsByProjectAndUserAndStatus(Project project, User user, RequestStatus status);
}