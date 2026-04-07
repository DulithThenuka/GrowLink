package com.example.GrowLink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.GrowLink.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}