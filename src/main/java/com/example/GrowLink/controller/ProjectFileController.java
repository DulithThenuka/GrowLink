package com.example.GrowLink.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.ProjectFile;
import com.example.GrowLink.service.ProjectFileService;

@Controller
@RequestMapping("/projects/{projectId}/files")
public class ProjectFileController {

    private final ProjectFileService projectFileService;

    public ProjectFileController(ProjectFileService projectFileService) {
        this.projectFileService = projectFileService;
    }

    @GetMapping
    public String showProjectFilesPage(@PathVariable Long projectId,
                                       Model model,
                                       Principal principal,
                                       @RequestParam(value = "message", required = false) String message) {

        Project project = projectFileService.getProjectById(projectId);

        boolean isOwner = false;
        boolean isMember = false;

        if (principal != null) {
            isOwner = projectFileService.isOwner(principal.getName(), projectId);
            isMember = projectFileService.isMember(principal.getName(), projectId);
        }

        if (!isOwner && !isMember) {
            return "redirect:/projects/" + projectId + "?message=" +
                    URLEncoder.encode("You do not have access to project files.", StandardCharsets.UTF_8);
        }

        model.addAttribute("project", project);
        model.addAttribute("files", projectFileService.getFilesByProjectId(projectId));
        model.addAttribute("fileCount", projectFileService.getFileCount(projectId));
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isMember", isMember);
        model.addAttribute("projectFileService", projectFileService);
        model.addAttribute("message", message);

        return "projects/project-files";
    }

    @PostMapping("/upload")
    public String uploadFile(@PathVariable Long projectId,
                             @RequestParam("file") MultipartFile file,
                             Principal principal) {

        String message = projectFileService.uploadFile(projectId, principal.getName(), file);

        return "redirect:/projects/" + projectId + "/files?message=" +
                URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    @GetMapping("/{fileId}/download")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable Long projectId,
                                                 @PathVariable Long fileId,
                                                 Principal principal) {

        ProjectFile projectFile = projectFileService.getProjectFileById(fileId);
        Resource resource = projectFileService.downloadFile(projectId, fileId, principal.getName());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + projectFile.getOriginalFileName() + "\"")
                .body(resource);
    }

    @PostMapping("/{fileId}/delete")
    public String deleteFile(@PathVariable Long projectId,
                             @PathVariable Long fileId,
                             Principal principal) {

        String message = projectFileService.deleteFile(projectId, fileId, principal.getName());

        return "redirect:/projects/" + projectId + "/files?message=" +
                URLEncoder.encode(message, StandardCharsets.UTF_8);
    }
}