package com.example.GrowLink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.ProjectMember;
import com.example.GrowLink.entity.User;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    List<ProjectMember> findByProjectOrderByIdAsc(Project project);

    List<ProjectMember> findByUserOrderByIdDesc(User user);

    Optional<ProjectMember> findByProjectAndUser(Project project, User user);

    boolean existsByProjectAndUser(Project project, User user);

    void deleteByProjectAndUser(Project project, User user);
}