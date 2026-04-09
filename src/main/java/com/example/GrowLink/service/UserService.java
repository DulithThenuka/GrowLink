package com.example.GrowLink.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.GrowLink.dto.ProfileUpdateDto;
import com.example.GrowLink.dto.RegisterDto;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.Role;
import com.example.GrowLink.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadService fileUploadService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       FileUploadService fileUploadService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileUploadService = fileUploadService;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User registerUser(RegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        User user = new User();
        user.setFullName(registerDto.getFullName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public ProfileUpdateDto getProfileUpdateDtoByEmail(String email) {
        User user = getUserByEmail(email);

        ProfileUpdateDto dto = new ProfileUpdateDto();
        dto.setFullName(user.getFullName());
        dto.setHeadline(user.getHeadline());
        dto.setLocation(user.getLocation());
        dto.setBio(user.getBio());

        return dto;
    }

    @Transactional
    public void updateProfile(String email, ProfileUpdateDto profileUpdateDto) {
        User user = getUserByEmail(email);

        user.setFullName(profileUpdateDto.getFullName());
        user.setHeadline(profileUpdateDto.getHeadline());
        user.setLocation(profileUpdateDto.getLocation());
        user.setBio(profileUpdateDto.getBio());

        if (profileUpdateDto.getProfileImageFile() != null
                && !profileUpdateDto.getProfileImageFile().isEmpty()) {
            String imagePath = fileUploadService.saveProfileImage(profileUpdateDto.getProfileImageFile());
            user.setProfileImage(imagePath);
        }

        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getDisabledUserCount() {
        return userRepository.countByEnabledFalse();
    }

    @Transactional
    public void disableUser(Long userId) {
        User user = getUserById(userId);
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    public void enableUser(Long userId) {
        User user = getUserById(userId);
        user.setEnabled(true);
        userRepository.save(user);
    }
    
}