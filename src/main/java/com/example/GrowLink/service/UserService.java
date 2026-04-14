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
import com.example.GrowLink.repository.UserLearnSkillRepository;
import com.example.GrowLink.repository.UserTeachSkillRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadService fileUploadService;

    // ✅ REPLACED SkillService with repositories
    private final UserTeachSkillRepository userTeachSkillRepository;
    private final UserLearnSkillRepository userLearnSkillRepository;

    public UserService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        FileUploadService fileUploadService,
                        UserTeachSkillRepository userTeachSkillRepository,
                        UserLearnSkillRepository userLearnSkillRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileUploadService = fileUploadService;
        this.userTeachSkillRepository = userTeachSkillRepository;
        this.userLearnSkillRepository = userLearnSkillRepository;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User registerUser(RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    // ✅ FIXED recommendation (NO SkillService)
    public List<User> getRecommendedUsers(String email) {

        User currentUser = getUserByEmail(email);
        List<User> allUsers = userRepository.findAll();

        Set<String> myTeach = new LinkedHashSet<>();
        Set<String> myLearn = new LinkedHashSet<>();

        userTeachSkillRepository.findByUser(currentUser).forEach(s ->
                myTeach.add(s.getSkill().getName().toLowerCase())
        );

        userLearnSkillRepository.findByUser(currentUser).forEach(s ->
                myLearn.add(s.getSkill().getName().toLowerCase())
        );

        List<User> result = new ArrayList<>();

        for (User other : allUsers) {
            if (other.getId().equals(currentUser.getId())) continue;

            Set<String> otherTeach = new LinkedHashSet<>();
            Set<String> otherLearn = new LinkedHashSet<>();

            userTeachSkillRepository.findByUser(other).forEach(s ->
                    otherTeach.add(s.getSkill().getName().toLowerCase())
            );

            userLearnSkillRepository.findByUser(other).forEach(s ->
                    otherLearn.add(s.getSkill().getName().toLowerCase())
            );

            boolean match =
                    myLearn.stream().anyMatch(otherTeach::contains)
                            || myTeach.stream().anyMatch(otherLearn::contains);

            if (match) result.add(other);
        }

        return result;
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
    public List<User> searchUsers(String keyword) {
    List<User> users = userRepository.findAll();

    if (keyword == null || keyword.trim().isEmpty()) {
        return users;
    }

    String lowerKeyword = keyword.toLowerCase();

    List<User> filtered = new ArrayList<>();

    for (User user : users) {
        if (user.getFullName() != null && user.getFullName().toLowerCase().contains(lowerKeyword)) {
            filtered.add(user);
        } else if (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerKeyword)) {
            filtered.add(user);
        }
    }

    return filtered;
}

public boolean emailExists(String email) {
    return userRepository.existsByEmail(email);
}
    @Transactional
    public void disableUser(Long id) {
        User user = getUserById(id);
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    public void enableUser(Long id) {
        User user = getUserById(id);
        user.setEnabled(true);
        userRepository.save(user);
    }
}