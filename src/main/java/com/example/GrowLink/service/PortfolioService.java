package com.example.GrowLink.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.GrowLink.dto.PortfolioProjectDto;
import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.ProjectMember;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.ProjectRole;
import com.example.GrowLink.enums.ProjectStatus;
import com.example.GrowLink.repository.ProjectMemberRepository;
import com.example.GrowLink.repository.ProjectRepository;

@Service
@Transactional(readOnly = true)
public class PortfolioService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public PortfolioService(ProjectRepository projectRepository,
                            ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    public List<PortfolioProjectDto> getPortfolioProjects(User profileUser) {
        Map<Long, PortfolioProjectDto> portfolioMap = new LinkedHashMap<>();

        List<Project> ownedCompletedProjects = projectRepository.findByOwnerAndStatusOrderByIdDesc(
                profileUser, ProjectStatus.COMPLETED
        );

        for (Project project : ownedCompletedProjects) {
            PortfolioProjectDto dto = new PortfolioProjectDto();
            dto.setProjectId(project.getId());
            dto.setTitle(project.getTitle());
            dto.setDescription(project.getDescription());
            dto.setCategory(project.getCategory());
            dto.setRole(ProjectRole.OWNER.name());
            dto.setStatus(project.getStatus() != null ? project.getStatus().name() : "COMPLETED");
            portfolioMap.put(project.getId(), dto);
        }

        List<ProjectMember> memberships = projectMemberRepository.findByUserOrderByIdDesc(profileUser);

        for (ProjectMember membership : memberships) {
            Project project = membership.getProject();

            if (project == null || project.getId() == null) {
                continue;
            }

            if (project.getStatus() != ProjectStatus.COMPLETED) {
                continue;
            }

            if (portfolioMap.containsKey(project.getId())) {
                continue;
            }

            PortfolioProjectDto dto = new PortfolioProjectDto();
            dto.setProjectId(project.getId());
            dto.setTitle(project.getTitle());
            dto.setDescription(project.getDescription());
            dto.setCategory(project.getCategory());
            dto.setRole(membership.getRole() != null ? membership.getRole().name() : ProjectRole.MEMBER.name());
            dto.setStatus(project.getStatus() != null ? project.getStatus().name() : "COMPLETED");

            portfolioMap.put(project.getId(), dto);
        }

        return new ArrayList<>(portfolioMap.values());
    }

    public long getPortfolioCount(User profileUser) {
        return getPortfolioProjects(profileUser).size();
    }
}