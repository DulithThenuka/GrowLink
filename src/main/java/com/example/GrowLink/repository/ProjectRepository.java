package com.example.GrowLink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOwner(User owner);

    List<Project> findByTitleContainingIgnoreCase(String title);

    List<Project> findByCategoryContainingIgnoreCase(String category);

    List<Project> findByTitleContainingIgnoreCaseAndCategoryContainingIgnoreCase(String title, String category);
}