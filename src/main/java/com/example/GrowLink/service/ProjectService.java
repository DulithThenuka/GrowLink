package com.example.GrowLink.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.GrowLink.dto.ProjectDto;
import com.example.GrowLink.entity.*;
import com.example.GrowLink.enums.ProjectRole;
import com.example.GrowLink.enums.RequestStatus;
import com.example.GrowLink.repository.*;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final ProjectJoinRequestRepository requestRepository;
    private final UserService userService;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMemberRepository memberRepository,
                          ProjectJoinRequestRepository requestRepository,
                          UserService userService) {
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.requestRepository = requestRepository;
        this.userService = userService;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public void createProject(String email, ProjectDto dto) {
        User user = userService.getUserByEmail(email);

        Project project = new Project();
        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setCategory(dto.getCategory());
        project.setStatus("OPEN");
        project.setOwner(user);

        projectRepository.save(project);

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setRole(ProjectRole.OWNER);

        memberRepository.save(member);
    }

    public void joinProject(String email, Long projectId) {
        User user = userService.getUserByEmail(email);
        Project project = projectRepository.findById(projectId).orElse(null);

        if (project == null) return;

        ProjectJoinRequest req = new ProjectJoinRequest();
        req.setProject(project);
        req.setUser(user);
        req.setStatus(RequestStatus.PENDING);

        requestRepository.save(req);
    }

    public void acceptRequest(Long requestId) {
        ProjectJoinRequest req = requestRepository.findById(requestId).orElse(null);
        if (req == null) return;

        req.setStatus(RequestStatus.ACCEPTED);

        ProjectMember member = new ProjectMember();
        member.setProject(req.getProject());
        member.setUser(req.getUser());
        member.setRole(ProjectRole.MEMBER);

        memberRepository.save(member);
    }
}