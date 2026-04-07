package com.example.GrowLink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.GrowLink.entity.User;
import com.example.GrowLink.entity.UserTeachSkill;

@Repository
public interface UserTeachSkillRepository extends JpaRepository<UserTeachSkill, Long> {

    List<UserTeachSkill> findByUser(User user);
}