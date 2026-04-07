package com.example.GrowLink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.GrowLink.entity.User;
import com.example.GrowLink.entity.UserLearnSkill;

@Repository
public interface UserLearnSkillRepository extends JpaRepository<UserLearnSkill, Long> {

    List<UserLearnSkill> findByUser(User user);
}