package com.example.GrowLink.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.GrowLink.dto.ProfileUpdateDto;
import com.example.GrowLink.dto.RegisterDto;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.Role;
import com.example.GrowLink.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User registerUser(RegisterDto registerDto) {
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

    public void updateProfile(String email, ProfileUpdateDto profileUpdateDto) {
        User user = getUserByEmail(email);

        user.setFullName(profileUpdateDto.getFullName());
        user.setHeadline(profileUpdateDto.getHeadline());
        user.setLocation(profileUpdateDto.getLocation());
        user.setBio(profileUpdateDto.getBio());

        userRepository.save(user);
    }
}