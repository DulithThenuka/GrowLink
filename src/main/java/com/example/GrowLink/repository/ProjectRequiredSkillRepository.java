package com.example.GrowLink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.ProjectRequiredSkill;

@Repository
public interface ProjectRequiredSkillRepository extends JpaRepository<ProjectRequiredSkill, Long> {

    List<ProjectRequiredSkill> findByProject(Project project);

    void deleteByProject(Project project);
}