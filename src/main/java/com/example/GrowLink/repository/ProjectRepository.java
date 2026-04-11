package com.example.GrowLink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.ProjectStatus;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOwner(User owner);

    List<Project> findByTitleContainingIgnoreCase(String title);

    List<Project> findByCategoryContainingIgnoreCase(String category);

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByTitleContainingIgnoreCaseAndCategoryContainingIgnoreCase(String title, String category);

    List<Project> findByTitleContainingIgnoreCaseAndStatus(String title, ProjectStatus status);

    List<Project> findByCategoryContainingIgnoreCaseAndStatus(String category, ProjectStatus status);

    List<Project> findByTitleContainingIgnoreCaseAndCategoryContainingIgnoreCaseAndStatus(
            String title,
            String category,
            ProjectStatus status
    );
}