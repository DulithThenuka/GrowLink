package com.example.GrowLink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.ProjectMessage;

public interface ProjectMessageRepository extends JpaRepository<ProjectMessage, Long> {

    List<ProjectMessage> findByProjectOrderByIdDesc(Project project);

    long countByProject(Project project);
}