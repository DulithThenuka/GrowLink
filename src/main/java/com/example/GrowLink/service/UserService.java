package com.example.GrowLink.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private final SkillService skillService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       FileUploadService fileUploadService,
                       SkillService skillService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileUploadService = fileUploadService;
        this.skillService = skillService;
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

    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return userRepository.findAll();
        }

        String cleanedKeyword = keyword.trim();
        return userRepository.findByFullNameContainingIgnoreCaseOrHeadlineContainingIgnoreCase(
                cleanedKeyword,
                cleanedKeyword
        );
    }

    public List<User> getRecommendedUsers(String email) {
        User currentUser = getUserByEmail(email);
        List<User> allUsers = userRepository.findAll();

        Set<String> myTeachSkills = new LinkedHashSet<>();
        Set<String> myLearnSkills = new LinkedHashSet<>();

        skillService.getTeachSkillsByUserEmail(email).forEach(item -> {
            if (item.getSkill() != null && item.getSkill().getName() != null) {
                myTeachSkills.add(item.getSkill().getName().trim().toLowerCase());
            }
        });

        skillService.getLearnSkillsByUserEmail(email).forEach(item -> {
            if (item.getSkill() != null && item.getSkill().getName() != null) {
                myLearnSkills.add(item.getSkill().getName().trim().toLowerCase());
            }
        });

        List<User> recommendedUsers = new ArrayList<>();

        for (User otherUser : allUsers) {
            if (otherUser.getId().equals(currentUser.getId())) {
                continue;
            }

            Set<String> otherTeachSkills = new LinkedHashSet<>();
            Set<String> otherLearnSkills = new LinkedHashSet<>();

            skillService.getTeachSkillsByUserEmail(otherUser.getEmail()).forEach(item -> {
                if (item.getSkill() != null && item.getSkill().getName() != null) {
                    otherTeachSkills.add(item.getSkill().getName().trim().toLowerCase());
                }
            });

            skillService.getLearnSkillsByUserEmail(otherUser.getEmail()).forEach(item -> {
                if (item.getSkill() != null && item.getSkill().getName() != null) {
                    otherLearnSkills.add(item.getSkill().getName().trim().toLowerCase());
                }
            });

            boolean matchFound = false;

            for (String skill : myLearnSkills) {
                if (otherTeachSkills.contains(skill)) {
                    matchFound = true;
                    break;
                }
            }

            if (!matchFound) {
                for (String skill : myTeachSkills) {
                    if (otherLearnSkills.contains(skill)) {
                        matchFound = true;
                        break;
                    }
                }
            }

            if (matchFound) {
                recommendedUsers.add(otherUser);
            }
        }

        return recommendedUsers;
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