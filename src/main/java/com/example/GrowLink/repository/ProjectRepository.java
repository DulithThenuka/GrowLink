package com.example.GrowLink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.User;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOwnerOrderByIdDesc(User owner);

    List<Project> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByIdDesc(String title, String description);

    List<Project> findByCategoryIgnoreCaseOrderByIdDesc(String category);

    List<Project> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndCategoryIgnoreCaseOrderByIdDesc(
            String title, String description, String category
    );
}