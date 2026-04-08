package com.example.GrowLink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.ProjectJoinRequest;
import com.example.GrowLink.entity.User;

@Repository
public interface ProjectJoinRequestRepository extends JpaRepository<ProjectJoinRequest, Long> {

    List<ProjectJoinRequest> findByProject(Project project);

    List<ProjectJoinRequest> findByUser(User user);

    Optional<ProjectJoinRequest> findByProjectAndUser(Project project, User user);
}