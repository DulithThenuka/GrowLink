package com.example.GrowLink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.ProjectFile;

public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long> {

    List<ProjectFile> findByProjectOrderByIdDesc(Project project);

    long countByProject(Project project);
}