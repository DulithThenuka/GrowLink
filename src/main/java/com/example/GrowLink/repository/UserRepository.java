package com.example.GrowLink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.GrowLink.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByEnabledFalse();

    List<User> findByFullNameContainingIgnoreCase(String name);

    List<User> findByHeadlineContainingIgnoreCase(String headline);

    List<User> findByFullNameContainingIgnoreCaseOrHeadlineContainingIgnoreCase(String fullName, String headline);
}