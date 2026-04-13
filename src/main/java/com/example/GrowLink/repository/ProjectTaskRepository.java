package com.example.GrowLink.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.ProjectTask;
import com.example.GrowLink.enums.TaskPriority;
import com.example.GrowLink.enums.TaskStatus;

public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {

    List<ProjectTask> findByProjectOrderByIdDesc(Project project);

    List<ProjectTask> findByProjectAndStatusOrderByIdDesc(Project project, TaskStatus status);

    long countByProjectAndStatus(Project project, TaskStatus status);

    long countByProject(Project project);

    long countByProjectAndDueDateBeforeAndStatusNot(Project project, LocalDate date, TaskStatus status);

    long countByProjectAndAssignedUserIsNull(Project project);

    long countByProjectAndPriority(Project project, TaskPriority priority);
}