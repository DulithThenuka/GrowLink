package com.example.GrowLink.service;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String newFilename = UUID.randomUUID() + extension;
            Path targetLocation = uploadPath.resolve(newFilename);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + newFilename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
}