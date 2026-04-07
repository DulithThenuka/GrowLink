package com.example.GrowLink.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.GrowLink.dto.LearnSkillDto;
import com.example.GrowLink.dto.TeachSkillDto;
import com.example.GrowLink.entity.Skill;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.entity.UserLearnSkill;
import com.example.GrowLink.entity.UserTeachSkill;
import com.example.GrowLink.repository.SkillRepository;
import com.example.GrowLink.repository.UserLearnSkillRepository;
import com.example.GrowLink.repository.UserTeachSkillRepository;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserTeachSkillRepository userTeachSkillRepository;
    private final UserLearnSkillRepository userLearnSkillRepository;
    private final UserService userService;

    public SkillService(SkillRepository skillRepository,
                        UserTeachSkillRepository userTeachSkillRepository,
                        UserLearnSkillRepository userLearnSkillRepository,
                        UserService userService) {
        this.skillRepository = skillRepository;
        this.userTeachSkillRepository = userTeachSkillRepository;
        this.userLearnSkillRepository = userLearnSkillRepository;
        this.userService = userService;
    }

    public List<UserTeachSkill> getTeachSkillsByUserEmail(String email) {
        User user = userService.getUserByEmail(email);
        return userTeachSkillRepository.findByUser(user);
    }

    public List<UserLearnSkill> getLearnSkillsByUserEmail(String email) {
        User user = userService.getUserByEmail(email);
        return userLearnSkillRepository.findByUser(user);
    }

    @Transactional
    public void addTeachSkill(String email, TeachSkillDto teachSkillDto) {
        User user = userService.getUserByEmail(email);

        Skill skill = findOrCreateSkill(
                teachSkillDto.getSkillName(),
                teachSkillDto.getCategory()
        );

        UserTeachSkill userTeachSkill = new UserTeachSkill();
        userTeachSkill.setUser(user);
        userTeachSkill.setSkill(skill);
        userTeachSkill.setLevel(teachSkillDto.getLevel());
        userTeachSkill.setExperienceText(teachSkillDto.getExperienceText());

        userTeachSkillRepository.save(userTeachSkill);
    }

    @Transactional
    public void addLearnSkill(String email, LearnSkillDto learnSkillDto) {
        User user = userService.getUserByEmail(email);

        Skill skill = findOrCreateSkill(
                learnSkillDto.getSkillName(),
                learnSkillDto.getCategory()
        );

        UserLearnSkill userLearnSkill = new UserLearnSkill();
        userLearnSkill.setUser(user);
        userLearnSkill.setSkill(skill);
        userLearnSkill.setTargetLevel(learnSkillDto.getTargetLevel());
        userLearnSkill.setNote(learnSkillDto.getNote());

        userLearnSkillRepository.save(userLearnSkill);
    }

    private Skill findOrCreateSkill(String skillName, String category) {
        return skillRepository.findByNameIgnoreCase(skillName.trim())
                .orElseGet(() -> {
                    Skill skill = new Skill();
                    skill.setName(skillName.trim());
                    skill.setCategory(category != null ? category.trim() : null);
                    skill.setDescription(null);
                    return skillRepository.save(skill);
                });
    }
}